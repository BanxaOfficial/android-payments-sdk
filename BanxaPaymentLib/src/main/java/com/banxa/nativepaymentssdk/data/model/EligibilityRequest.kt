package com.banxa.nativepaymentssdk.data.model

data class EligibilityRequest(
    val crypto: String,
    val fiat: String,
    val walletAddress: String,
    val email: String,
    val redirectUrl: String,
    val fiatAmount: String,
    val paymentMethodId: String,
)
