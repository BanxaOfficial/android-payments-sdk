package com.banxa.nativepaymentssdk.data.model

data class BanxaError(
    val message: String,
    val code: String? = null,
    val statusCode: Int? = null,
    val cause: Any? = null,
    val details: Map<String, Any?>? = null
)