package com.banxa.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.banxa.ui.ui.theme.BanxaPaymentSDKNewTheme

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Message
import android.util.Log
import android.webkit.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView

class TestWebviewCheckActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BanxaPaymentSDKNewTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        PaymentWebView("https://spa.banxa-preprod.com/papi/transit/?initId=eyJpdiI6ImlySWkzY1VnamtpcEhhcmJ2Zk9Ob1E9PSIsInZhbHVlIjoiZit4cDJpVVBpMnRkWmV4c0NQVm4yMlZtNDE1ejgxZ3Q0NExkYnU4dGVJVVZMZkNrZTFrZ2Y4MVBSekt0c1F1ZGQ3dmhMM05GMnNDVUdlYmEvbTREQzhtZ1NOWXBqWk9Qek5QWDdVdCtNd1pYS1FHZHlPQ2hjTy84RWNoYlVJS2k1VDZidVZwUllrM214b3BFT1NsL3g5UG1yYlllMHMybkFWRSszVXJQVUx3NWZwbHpkKzFhL09BVzBhMjdlekpTVXA4MDBydHRnS1VFbHlUWW9iTTJENDJNcTIrTXdBYjBTU0dQSWkxZW9TbDN5RXJ6SVFMdXhUeHdCK0pmM1ZRVXcxZlZrYU5uLy91RFpUbFBOQzhCeVlscTE0WU56Smk2YVRUY1IyaENWdW5PdFRNMmVOdUJWWGNkWE0vOCtlRXZHYXFBUUNqMGlNRDBpdUZlUHJvTjliSytjeVRqOUxtV1kvRmNNUjZuVmZjY0QxNE0yOEtjaUdOVkh3WlpEZFFLNFcxOHkrUEc2S1o4U2hJcXZGSjVBanlhSzdwbzBxNWxzcHc2SUx3ekZPbGVRSTQ2VGVqeHdoRkg3eWFMbEV4ZSIsIm1hYyI6ImY0MzUxYTU2Njk2NmEyMDk5NTMwMmNjYmU5NDUyYjNhNTYxZTA0MGJjZTBiM2ExMGRhODQ5YmIyOTU1YTI4NjAiLCJ0YWciOiIifQ==")
                    }
                }
            }
        }
    }
}



@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PaymentWebView(
    url: String
) {

    var webView by remember { mutableStateOf<WebView?>(null) }

    AndroidView(
        factory = { context ->
            WebView(context).apply {

                webView = this

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
                        if(newUrl.startsWith("http")) {
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
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BanxaPaymentSDKNewTheme {
        Greeting("Android")
    }
}