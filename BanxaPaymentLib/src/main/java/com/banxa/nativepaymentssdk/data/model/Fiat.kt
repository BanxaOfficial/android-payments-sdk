package com.banxa.nativepaymentssdk.data.model

/** A fiat currency returned by GET /{partner}/v2/fiats/{orderType}. */
data class Fiat(
    val id: String,
    val description: String,
    val symbol: String? = null,
    val supportedPaymentMethods: List<FiatPaymentMethod> = emptyList()
)

/** Payment method availability and limits for a [Fiat]. */
data class FiatPaymentMethod(
    val id: String,
    val name: String,
    val minimum: String? = null,
    val maximum: String? = null
)
