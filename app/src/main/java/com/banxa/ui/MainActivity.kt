package com.banxa.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import io.primer.checkout.PrimerTheme
import io.primer.checkout.internal.tokens.LightColorTokens

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BanxaPaymentSDKNewTheme {
                var showBanxa = remember { mutableStateOf(false) }
                var showDialog by remember { mutableStateOf(false) }
                var message = remember { mutableStateOf("") }
                var paymentData1 = remember { mutableStateOf<PaymentData?>(null) }
                PaymentSdkManager.initialize(sdkConfig = PaymentSdkConfig(
                    lightColors = lightColorScheme(background = Color.Black)
                ))
                /*Column(
                    Modifier.fillMaxSize().padding(50.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                       Button(
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
                }*/
                BanxaPaymentScreen(enable1 = showBanxa.value.not()){
                    showBanxa.value = true
                    paymentData1.value = it
                }
                if(showBanxa.value){
                    BuyButtonClickedComposable(paymentData1.value){
                        message.value = it
                        showBanxa.value = false
                        showDialog = true
                    }
                }
                if(showDialog){
                    TransactionStatusAlert( message.value){
                        showDialog = false
                    }
                }
            }
        }
    }
}
data class PaymentData(
    val paymentMethodId: String,
    val crypto: String,
    val fiat: String,
    val fiatAmount: String,
    val walletAddress: String,
    val email: String,
    val redirectUrl: String
)
@Composable
fun BanxaPaymentScreen(
    enable1: Boolean = true,
    onPayClick: (PaymentData) -> Unit
) {
    var paymentMethod by remember { mutableStateOf("google-pay") }//debit-credit-card
    var crypto by remember { mutableStateOf("ETHH") }
    var fiat by remember { mutableStateOf("USD") }
    var amount by remember { mutableStateOf("100") }
    var walletAddress by remember { mutableStateOf("0x525b287bc1f30dbAEBAcd382C4a4E5795d16De7C") }
    var email by remember { mutableStateOf("zachary@banxa.com") }
    var redirectUrl by remember { mutableStateOf("https://banxa.com") }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        BanxaTextField("Payment Method ID", paymentMethod) { paymentMethod = it }
        BanxaTextField("Crypto", crypto) { crypto = it }
        BanxaTextField("Fiat", fiat) { fiat = it }
        BanxaTextField("Fiat Amount", amount) { amount = it }
        BanxaTextField("Wallet Address", walletAddress) { walletAddress = it }
        BanxaTextField("Email", email) { email = it }
        BanxaTextField("Redirect URL", redirectUrl) { redirectUrl = it }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val data = PaymentData(
                    paymentMethod,
                    crypto,
                    fiat,
                    amount,
                    walletAddress,
                    email,
                    redirectUrl
                )
                onPayClick(data)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black
            )
        ) {
            /*Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null
            )*/
            Spacer(modifier = Modifier.width(8.dp))
            if(enable1) {
                Text("Banxa Pay")
            }else{
                Text("Paying...")
            }
        }
        Spacer(modifier = Modifier.height(25.dp))
    }
}
@Composable
fun BanxaTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            shape = RoundedCornerShape(30.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.LightGray,
                unfocusedContainerColor = Color.LightGray,
                disabledContainerColor = Color.LightGray
            ),modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BuyButtonClickedComposable(
    paymentData: PaymentData?,
    onActionDone: (String) -> Unit
) {
    Log.i("paymentData", "paymentData: ${paymentData.toString()}")
    var banxa = getBanxa()

    Banxa.initialize(banxa)
    paymentData?.let {
        CreateOrder(
            createBuyOrderRequest = CreateBuyOrderRequest(
                fiat = paymentData.fiat,
                crypto = paymentData.crypto,
                fiatAmount = paymentData.fiatAmount,
                cryptoAmount = null,
                walletAddress = paymentData.walletAddress,
                walletAddressTag = null,
                redirectUrl = paymentData.redirectUrl,
                paymentMethodId = paymentData.paymentMethodId,
                blockchain = null,
                metadata = null,
                externalOrderId = null,
                subPartnerId = null,
                discountCode = null,
                email = paymentData.email
            ),
            onCheckoutComplete = {
                onActionDone.invoke("Checkout Completed.")
            },
            onError = {
                onActionDone.invoke(it)
            },
            onDismiss = {
                onActionDone.invoke("onDismiss Called")
            },
        )
    }
}

@Composable
fun TransactionStatusAlert(message: String,closeDialog: () -> Unit) {

        AlertDialog(
            onDismissRequest = {
                closeDialog.invoke()
            },
            title = {
                Text(text = "Alert")
            },
            text = {
                Text(message)
            },
            confirmButton = {
                Button(
                    onClick = {
                        closeDialog.invoke()
                    }
                ) {
                    Text("OK")
                }
            }
        )

}

fun getBanxa(): Banxa{
    val primerTheme = PrimerTheme(
        lightColorTokens = object : LightColorTokens() {
            override val primerColorBrand: Color = Color(0xFF6C5CE7)
            override val primerColorTextPrimary: Color = Color(0xFFD32E2E)
            override val primerColorBackground: Color = Color(0xFF9CFFA1)
        },
    )
    return Banxa.Builder()
        .apiKey("cadac59bbd3e45d7652738a24568856167655bff")
        .partner("demomerchant")
        .environment(Environment.SANDBOX)
        .primerTheme(primerTheme)
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