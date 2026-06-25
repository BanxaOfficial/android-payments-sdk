package com.banxa.nativepaymentssdk.data.model

data class CheckoutCompleteResponse(
    val code: Int,
    val message: String,
    val checkoutUrl: String = ""
)