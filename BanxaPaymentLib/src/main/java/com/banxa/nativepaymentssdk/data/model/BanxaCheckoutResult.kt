package com.banxa.nativepaymentssdk.data.model

data class BanxaCheckoutResult(
    val paymentId: String? = null,
    val orderId: String? = null,
    val status: String? = null,
    val rawQuery: String? = null
)