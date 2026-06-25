package com.banxa.nativepaymentssdk.ui


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.primer.checkout.PrimerTheme
import io.primer.checkout.api.checkout.PrimerCheckoutSheet
import io.primer.checkout.api.checkout.rememberPrimerCheckoutController
import io.primer.checkout.api.state.PrimerCheckoutEvent
import io.primer.checkout.internal.tokens.LightColorTokens

@Composable
fun PrimerCheckOutScreen(clientToken: String, primerTheme: PrimerTheme, onSuccessPrimerSDK : () -> Unit = {}, onFailurePrimerSDK : () -> Unit = {}, onDismiss : () -> Unit = {}) {
        val checkout = rememberPrimerCheckoutController(
            clientToken = clientToken,
        )

        PrimerCheckoutSheet(
            theme = primerTheme,
            onDismiss ={
                onDismiss.invoke()
            },
            /*splash = {
                Column(modifier = Modifier
                    .height(300.dp)
                ){
                    Text("This is Splash Screen ...")
                }
            },*/
            success = {
                //it.payment.id
            },
            checkout = checkout,
            onEvent = { event ->
                when(event) {
                    is PrimerCheckoutEvent.Success -> {
                        //event.checkoutData.payment.id
                    }
                    is PrimerCheckoutEvent.Failure -> {
                        Log.i("PD","Error : ")
                    }
                }
            },
        )
}