package com.banxa.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.banxa.nativepaymentssdk.core.AppearanceMode
import com.banxa.nativepaymentssdk.core.Banxa
import com.banxa.nativepaymentssdk.core.Colors
import com.banxa.nativepaymentssdk.core.Environment
import com.banxa.nativepaymentssdk.core.PaymentSdkConfig
import com.banxa.nativepaymentssdk.core.PrimerSettings
import com.banxa.nativepaymentssdk.core.RGBA
import com.banxa.nativepaymentssdk.core.Theme
import com.banxa.nativepaymentssdk.core.UiOptions
import com.banxa.nativepaymentssdk.data.model.CreateBuyOrderRequest
import com.banxa.nativepaymentssdk.theme.PaymentSdkManager
import com.banxa.nativepaymentssdk.ui.CreateOrder
import com.banxa.ui.theme.BanxaPaymentSDKNewTheme
import kotlin.String

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BanxaPaymentSDKNewTheme {
                var showBanxa = remember { mutableStateOf(false) }
                PaymentSdkManager.initialize(sdkConfig = PaymentSdkConfig(
                    lightColors = lightColorScheme(background = Color.Black)
                ))
                Column(
                    Modifier.fillMaxSize().padding(50.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                        androidx.compose.material3.Button(
                            enabled = showBanxa.value.not(),
                            onClick = {
                            showBanxa.value = true
                        }){
                            Text(if(showBanxa.value) "Buying..." else "Buy with Banxa")
                        }
                        if(showBanxa.value){
                            BuyButtonClickedComposable{
                                showBanxa.value = false
                            }
                        }
                }
            }
        }
    }
}

@Composable
fun BuyButtonClickedComposable(onActionDone: () -> Unit){
    var banxa = getBanxa()

    Banxa.initialize(banxa)

    CreateOrder(
        createBuyOrderRequest = CreateBuyOrderRequest(
             externalCustomerId  = "",
             fiat = "EUR",
             crypto = "ETH",
             fiatAmount = "40",
             cryptoAmount = null,
             walletAddress = "0xea450e9119bcefff2c1e51849a8b18f9546d12e1",
             walletAddressTag= null,
             redirectUrl = "https://example.com",
             paymentMethodId= "apple-pay",
             blockchain = null,
             metadata= null,
             externalOrderId= null,
             subPartnerId = null,
             discountCode= null,
             email= "test-valuelabs@banxa.com"
        ),
        onCheckoutComplete = {
            onActionDone.invoke()
        },
        onError = {
            onActionDone.invoke()
        },
        onDismiss = {
            onActionDone.invoke()
        },
    )
}

fun getBanxa(): Banxa{
    return Banxa.Builder()
        .apiKey("cadac59bbd3e45d7652738a24568856167655bff")
        .partner("demomerchant")
        .environment(Environment.SANDBOX)
        .primerSettings(
            PrimerSettings(
                uiOptions = UiOptions(
                    appearanceMode = AppearanceMode.DARK,
                    theme = Theme(
                        colors = Colors(
                            mainColor = RGBA(1, 255, 1, 255),
                            contrastingColor = RGBA(23, 196, 200, 255),
                            background = RGBA(234, 234, 235, 255),
                            containerColor = RGBA(225, 225, 226, 255),
                            text = RGBA(17, 22, 48, 255),
                            contrastingText = RGBA(0, 0, 0, 255),
                            borders = RGBA(255, 255, 255, 255),
                            disabled = RGBA(91, 93, 112, 255),
                            error = RGBA(255, 0, 0, 255)
                        ),

                        darkModeColors = Colors(
                            mainColor = RGBA(1, 255, 1, 255),
                            contrastingColor = RGBA(23, 196, 200, 255),
                            background = RGBA(26, 31, 56, 255),
                            containerColor = RGBA(35, 40, 64, 255),
                            text = RGBA(255, 255, 255, 255),
                            contrastingText = RGBA(255, 255, 255, 255),
                            borders = RGBA(255, 255, 255, 255),
                            disabled = RGBA(91, 93, 112, 255),
                            error = RGBA(255, 0, 0, 255)
                        )
                    )
                )
            )
        )
        .build()
}