package com.banxa.nativepaymentssdk.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Message
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.banxa.nativepaymentssdk.data.model.CheckoutWebViewState

/**
 * CheckoutWebViewScreen
 *
 * Full-screen Jetpack Compose equivalent of `src/components/CheckoutWebView.tsx`.
 *
 * Renders the Banxa hosted checkout URL inside a [WebView] with an OkHttp-style
 * deep-link interceptor that mirrors the `onNavigationStateChange` handler in the
 * React Native implementation.
 *
 * Usage:
 * ```kotlin
 * if (showWebView) {
 *     CheckoutWebViewScreen(
 *         state     = webViewState,
 *         onSuccess = { orderId -> /* handle success */ },
 *         onFailure = { orderId -> /* handle failure */ },
 *         onDismiss = { showWebView = false }
 *     )
 * }
 * ```
 *
 * @param state       Data from [BuyRepository] / [SellRepository] describing the URL
 *                    and the success/failure deep-link patterns to intercept.
 * @param onSuccess   Called with the Banxa order-id when the success URL is matched.
 * @param onFailure   Called with the Banxa order-id when the failure URL is matched.
 * @param onDismiss   Called when the user taps the Close (✕) button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CheckoutWebViewScreen(
    state: CheckoutWebViewState? = null,
    url: String = "",
    onSuccess: (rawQuery: String) -> Unit = {},
    onFailure: (rawQuery: String) -> Unit = {},
    onDismiss: () -> Unit ={}
) {
    var isLoading by remember { mutableStateOf(true) }
        Box(
            modifier = Modifier
                .fillMaxSize()
               // .padding(paddingValues)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true

                            // Required for payment popups
                            javaScriptCanOpenWindowsAutomatically = true
                            setSupportMultipleWindows(true)

                            // Improve compatibility
                            loadWithOverviewMode = true
                            useWideViewPort = true

                            // Enable mixed content (sandbox environments often need it)
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                            // Allow third-party cookies (important for payments)
                            //CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
                        }

                        // Handle page loading inside WebView
                        webViewClient = object : WebViewClient() {

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val newUrl = request?.url.toString()

                                if(newUrl.contains("/status/")){
                                    onSuccess.invoke(newUrl)
                                }

                                if (newUrl.contains("failure")) {
                                    onFailure.invoke(newUrl)
                                    return true
                                }

                                if (newUrl.contains("/error/")) {
                                    onFailure.invoke(newUrl)
                                    return true
                                }
                                return true
                            }

                            override fun doUpdateVisitedHistory(
                                view: WebView?,
                                url: String?,
                                isReload: Boolean
                            ) {
                                super.doUpdateVisitedHistory(view, url, isReload)
                                url?.let {
                                    if(it.contains("/error/")){ //For Error
                                        onFailure.invoke(it)
                                    }
                                    if(it.contains("/status/")){ //For Success
                                        onSuccess.invoke(it)
                                    }
                                }
                            }
                        }

                        // Handle popups / JS windows
                        webChromeClient = object : WebChromeClient() {

                            override fun onCreateWindow(
                                view: WebView?,
                                isDialog: Boolean,
                                isUserGesture: Boolean,
                                resultMsg: Message?
                            ): Boolean {

                                val newWebView = WebView(context).apply {
                                    settings.javaScriptEnabled = true
                                }

                                val transport = resultMsg?.obj as WebView.WebViewTransport
                                transport.webView = newWebView
                                resultMsg.sendToTarget()

                                return true
                            }
                        }
                        loadUrl(url)
                    }
                },
                update = {
                    it.loadUrl(url)
                }
            )

            // Loading spinner overlay — shown until first page finishes loading
            if (isLoading) {
                //CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
   }
