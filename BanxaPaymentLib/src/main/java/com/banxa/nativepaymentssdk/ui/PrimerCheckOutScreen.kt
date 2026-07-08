package com.banxa.nativepaymentssdk.ui

import androidx.compose.runtime.Composable
import io.primer.checkout.PrimerTheme
import io.primer.checkout.api.checkout.PrimerCheckoutSheet
import io.primer.checkout.api.checkout.rememberPrimerCheckoutController
import io.primer.checkout.api.state.PrimerCheckoutEvent

@Composable
fun PrimerCheckOutScreen(
    clientToken: String,
    primerTheme: PrimerTheme?,
    onSuccessPrimerSDK: () -> Unit = {},
    onFailurePrimerSDK: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val checkout = rememberPrimerCheckoutController(
        clientToken = clientToken
    )
    PrimerCheckoutSheet(
        theme = primerTheme ?: PrimerTheme(),
        onDismiss = {
            onDismiss.invoke()
        },
        success = {
            //it.payment.id
            onSuccessPrimerSDK.invoke()
        },
        checkout = checkout,
        onEvent = { event ->
            when (event) {
                is PrimerCheckoutEvent.Success -> {
                    //event.checkoutData.payment.id
                    onSuccessPrimerSDK.invoke()
                }

                is PrimerCheckoutEvent.Failure -> {
                    onFailurePrimerSDK.invoke()
                }
            }
        },
    )
}