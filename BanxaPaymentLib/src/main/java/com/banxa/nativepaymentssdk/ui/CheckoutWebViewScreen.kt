package com.banxa.nativepaymentssdk.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Message
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    onSuccess: (orderId: String?) -> Unit = {},
    onFailure: (orderId: String?) -> Unit = {},
    onDismiss: () -> Unit ={}
) {
    var isLoading by remember { mutableStateOf(true) }
    //var url by remember { mutableStateOf("https://demomerchant.banxa-sandbox.com/papi/transit/?initId=eyJpdiI6ImpMcXJVQmpLT2pkQml0RnZZaEgvdWc9PSIsInZhbHVlIjoieVhsTXJHM1pCTXgrcmJTRTJsQ2VXcGFRWW40ZW13SDNEbFplWURNZUI3b0FKRGRiNTdzQnU1RTU5eThCOGNmNlJJVkVFNFpiZmZRZUhQZUlNbW9VZ0YrNWF6V2twdXpscnFZeXlVaENMa21FeUY3T1VNTFpoSjVhNjZrSWl6SHRpazZJWmV1NlVVK1BraElmWUhVc01haUxhY1plN2lMR3V6ekk0MVhRYkxjWWoyQll0OVhJMnZkdnE2NkNJRW1KNDJJTjBYT0pRdWRqdzl4cnB4R3VBYlVnVElDbzYxT1FYMVUxQ3hzcUVQL0FPYVNKV2kvdHh2TTd2ODFibTM4L3k3eEpOekFrVDRKbU81dDMySHV2NWhpNktKanVTMmsrdGh6ZlYxeVNkZ1BtVVBxMFc2UkpIZm1paXhxWm5ud1hCT2VPVmZjMjlXU1hKTlJVVzgvdXlMejZYT1lJcnVCR3hXNjRYZWNIOTc2cjFPNXZzRlRwSWk5aERWNVptVUJ6UnpmZHhYOVZOWlR2TXRuSzRzWTRtU2dqZ2tUdTdxVFdpMnZleGVJVkJEanZNQ243MUlpV2h0dWZTaG5zaG16UCIsIm1hYyI6IjJlY2Y3OTI1MDlmODdiNGIzZjY0NTA2MTk4ZjRhMTA3NzQ0YzRiYmQ4N2U2ODNmNzRjNTQ1OGEyNmUzMWRiYmUiLCJ0YWciOiIifQ==") }
   /* Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Banxa Checkout") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector   = Icons.Default.Close,
                            contentDescription = "Close checkout"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->*/
        Box(
            modifier = Modifier
                .fillMaxSize()
               // .padding(paddingValues)
        ) {
            AndroidView(
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
                                Log.i("PD99","newUrl : $newUrl")
                                // handle external redirects if needed
                                if (newUrl.startsWith("http")) {
                                    return false // load inside webview
                                }

                                return true
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
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
   }
/* }*/

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Attempts to extract a Banxa `orderId` query parameter from a deep-link URL.
 * Returns `null` when no such parameter exists.
 *
 * Example: `banxa://checkout/success?orderId=abc123` → `"abc123"`
 */
/*private fun extractOrderId(url: String): String? =
    runCatching {
        android.net.Uri.parse(url).getQueryParameter("orderId")
    }.getOrNull()*/
