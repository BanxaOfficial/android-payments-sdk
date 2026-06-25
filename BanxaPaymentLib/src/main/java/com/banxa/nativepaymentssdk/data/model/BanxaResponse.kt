package com.banxa.nativepaymentssdk.data.model

data class BanxaResponse<T>(
    val data: T,
    val message: String? = null
)