package com.banxa.nativepaymentssdk.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.RenderProcessGoneDetail
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.webkit.ScriptHandler
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import com.banxa.nativepaymentssdk.kyc.BanxaKycMessages
import com.banxa.nativepaymentssdk.kyc.HostedKycEvent
import com.banxa.nativepaymentssdk.kyc.HostedKycSessionMonitor
import com.banxa.nativepaymentssdk.kyc.NavigationDecision

private const val TAG = "BPS"
private const val BRIDGE_NAME = "banxaKyc"

/** Wraps `window.postMessage` so `banxa:*` messages reach the native bridge. */
private val POST_MESSAGE_BRIDGE = """
    (function () {
      if (window.__banxaKycBridge) return;
      window.__banxaKycBridge = true;
      var original = window.postMessage;
      window.postMessage = function (message) {
        try {
          if (message && typeof message === 'object' && typeof message.type === 'string'
              && message.type.indexOf('${BanxaKycMessages.PREFIX}') === 0 && window.$BRIDGE_NAME) {
            window.$BRIDGE_NAME.postMessage(JSON.stringify(message));
          }
        } catch (e) {}
        return original.apply(this, arguments);
      };
    })();
""".trimIndent()

/**
 * Shows the Banxa hosted KYC flow in a WebView.
 *
 * @param url
 * @param monitor
 * @param onEvent
 * @param onPermissionDenied
 * @param onRendererGone
 * @return
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun HostedKycWebViewScreen(
    url: String,
    monitor: HostedKycSessionMonitor,
    onEvent: (HostedKycEvent) -> Unit,
    onPermissionDenied: () -> Unit,
    onRendererGone: () -> Unit
) {
    val currentOnEvent by rememberUpdatedState(onEvent)
    val currentOnPermissionDenied by rememberUpdatedState(onPermissionDenied)
    val currentOnRendererGone by rememberUpdatedState(onRendererGone)

    var isLoading by remember { mutableStateOf(true) }
    var canGoBack by remember { mutableStateOf(false) }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var scriptHandler by remember { mutableStateOf<ScriptHandler?>(null) }

    // A page asks for the camera while the Android runtime permission is still missing.
    var pendingPermission by remember { mutableStateOf<PermissionRequest?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val request = pendingPermission ?: return@rememberLauncherForActivityResult
        pendingPermission = null
        grantPermittedResources(request, granted = { results[it] == true }, onDenied = currentOnPermissionDenied)
    }

    var pendingFileCallback by remember { mutableStateOf<ValueCallback<Array<Uri>>?>(null) }
    val fileChooserLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        pendingFileCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(result.resultCode, result.data))
        pendingFileCallback = null
    }

    BackHandler(enabled = canGoBack) { webView?.goBack() }

    val sheetHeight = LocalConfiguration.current.screenHeightDp.dp * 0.85f

    Box(modifier = Modifier.fillMaxWidth().height(sheetHeight)) {
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(sheetHeight),
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                        allowFileAccess = false
                        javaScriptCanOpenWindowsAutomatically = false
                        setSupportMultipleWindows(false)
                    }
                    // SumSub runs in a cross-origin iframe and keeps its session in cookies.
                    CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                    scriptHandler = installMessageBridge(this, monitor) { currentOnEvent(it) }

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                            if (request == null || !request.isForMainFrame) return false

                            val decision = monitor.handleNavigation(request.url.toString())
                            if (decision is NavigationDecision.Intercept) {
                                decision.completion?.let { currentOnEvent(it) }
                                return true
                            }

                            // Anything WebView can't render, such as intent: or tel:, would only show an error page.
                            return request.url.scheme !in setOf("http", "https")
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            isLoading = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                        }

                        override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                            canGoBack = view?.canGoBack() == true
                        }

                        // Document capture is memory hungry. Without this, a killed renderer takes the host app with it.
                        override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                            currentOnRendererGone()
                            return true
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onPermissionRequest(request: PermissionRequest) {
                            if (!monitor.shouldGrantMediaCapture(this@apply.url)) {
                                request.deny()
                                return
                            }

                            val missing = androidPermissionsFor(request.resources).filter {
                                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
                            }
                            if (missing.isEmpty()) {
                                grantPermittedResources(request, granted = { true }, onDenied = currentOnPermissionDenied)
                                return
                            }

                            pendingPermission?.deny()
                            pendingPermission = request
                            permissionLauncher.launch(missing.toTypedArray())
                        }

                        override fun onShowFileChooser(
                            view: WebView?,
                            filePathCallback: ValueCallback<Array<Uri>>?,
                            fileChooserParams: FileChooserParams?
                        ): Boolean {
                            if (filePathCallback == null || fileChooserParams == null) return false

                            // WebView won't open another chooser until the previous callback has been answered.
                            pendingFileCallback?.onReceiveValue(null)
                            pendingFileCallback = filePathCallback
                            return try {
                                fileChooserLauncher.launch(fileChooserParams.createIntent())
                                true
                            } catch (e: ActivityNotFoundException) {
                                pendingFileCallback = null
                                false
                            }
                        }
                    }

                    webView = this
                    loadUrl(url)
                }
            },
            onRelease = { view ->
                scriptHandler?.remove()
                if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
                    WebViewCompat.removeWebMessageListener(view, BRIDGE_NAME)
                }
                pendingPermission?.deny()
                pendingFileCallback?.onReceiveValue(null)
                view.stopLoading()
                view.destroy()
                webView = null
            }
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

/**
 * Installs the `postMessage` bridge and returns its script handle, or null when the WebView can't support it.
 *
 * @param webView
 * @param monitor
 * @param onEvent
 * @return
 */
private fun installMessageBridge(
    webView: WebView,
    monitor: HostedKycSessionMonitor,
    onEvent: (HostedKycEvent) -> Unit
): ScriptHandler? {
    val originRules = monitor.acceptedOrigins.toSet()

    return try {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER) &&
            WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)
        ) {
            WebViewCompat.addWebMessageListener(webView, BRIDGE_NAME, originRules) { _, message, sourceOrigin, isMainFrame, _ ->
                val data = message.data ?: return@addWebMessageListener
                monitor.handleMessage(data, sourceOrigin.toString(), isMainFrame)?.let(onEvent)
            }
            WebViewCompat.addDocumentStartJavaScript(webView, POST_MESSAGE_BRIDGE, originRules)
        } else {
            Log.w(TAG, "WebView lacks message listener support, so KYC completion relies on the return URL only")
            null
        }
    } catch (e: IllegalArgumentException) {
        // An allowed origin WebView can't turn into a rule, such as one with a path.
        Log.w(TAG, "Invalid KYC origin rules $originRules, so KYC completion relies on the return URL only", e)
        null
    }
}

/**
 * Maps WebView capture resources to the Android permissions they need.
 *
 * @param resources
 * @return
 */
private fun androidPermissionsFor(resources: Array<String>): List<String> = resources.mapNotNull {
    when (it) {
        PermissionRequest.RESOURCE_VIDEO_CAPTURE -> Manifest.permission.CAMERA
        PermissionRequest.RESOURCE_AUDIO_CAPTURE -> Manifest.permission.RECORD_AUDIO
        else -> null
    }
}

/**
 * Grants the capture resources whose Android permission is held, and reports a denied camera.
 *
 * @param request
 * @param granted
 * @param onDenied
 * @return
 */
private fun grantPermittedResources(
    request: PermissionRequest,
    granted: (String) -> Boolean,
    onDenied: () -> Unit
) {
    val grantable = request.resources.filter { resource ->
        val permission = androidPermissionsFor(arrayOf(resource)).firstOrNull() ?: return@filter false
        granted(permission)
    }

    if (PermissionRequest.RESOURCE_VIDEO_CAPTURE in request.resources &&
        PermissionRequest.RESOURCE_VIDEO_CAPTURE !in grantable
    ) {
        request.deny()
        onDenied()
        return
    }

    if (grantable.isEmpty()) {
        request.deny()
        return
    }

    request.grant(grantable.toTypedArray())
}

/**
 * Returns the permissions from [required] that the host app hasn't declared in its manifest.
 *
 * @param context
 * @param required
 * @return
 */
internal fun undeclaredPermissions(context: Context, required: List<String>): List<String> {
    val declared = try {
        context.packageManager
            .getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
            .requestedPermissions
            .orEmpty()
            .toSet()
    } catch (e: PackageManager.NameNotFoundException) {
        return emptyList()
    }

    return required.filterNot { it in declared }
}
