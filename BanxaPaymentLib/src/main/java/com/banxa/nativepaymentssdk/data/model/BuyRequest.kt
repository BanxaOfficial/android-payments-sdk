package com.banxa.nativepaymentssdk.data.model

data class BuyRequest(
    val paymentMethodId: String,
    val crypto: String,
    val fiat: String,
    val fiatAmount: String = "",
    val walletAddress: String,
    val walletAddressTag: String? = null,
    val redirectUrl: String,
    val subPartnerId: String? = null,
    val metadata: String? = null,
    val externalCustomerId: String? = null,
    val externalOrderId: String? = null,
    val discountCode: String? = null,
    val email: String
)