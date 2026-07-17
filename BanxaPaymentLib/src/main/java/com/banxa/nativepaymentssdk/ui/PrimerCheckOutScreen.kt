package com.banxa.nativepaymentssdk.ui

import androidx.compose.runtime.Composable
import io.primer.android.domain.PrimerCheckoutData
import io.primer.checkout.PrimerTheme
import io.primer.checkout.api.checkout.PrimerCheckoutSheet
import io.primer.checkout.api.checkout.rememberPrimerCheckoutController
import io.primer.checkout.api.state.PrimerCheckoutEvent

@Composable
fun PrimerCheckOutScreen(
    clientToken: String,
    primerTheme: PrimerTheme?,
    onSuccessPrimerSDK: (PrimerCheckoutData) -> Unit = {},
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
            onSuccessPrimerSDK.invoke(it)
        },
        checkout = checkout,
        onEvent = { event ->
            when (event) {
                is PrimerCheckoutEvent.Success -> {
                    //event.checkoutData.payment.id
                    onSuccessPrimerSDK.invoke(event.checkoutData)
                }

                is PrimerCheckoutEvent.Failure -> {
                    onFailurePrimerSDK.invoke()
                }
            }
        },
    )
}