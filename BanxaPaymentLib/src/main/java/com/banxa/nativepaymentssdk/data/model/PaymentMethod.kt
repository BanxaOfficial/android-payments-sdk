package com.banxa.nativepaymentssdk.data.model

/** A payment method returned by GET /{partner}/v2/payment-methods/{orderType}. */
data class PaymentMethod(
    val id: String,
    val name: String,
    val description: String? = null,
    val supportedFiats: List<String> = emptyList()
)
