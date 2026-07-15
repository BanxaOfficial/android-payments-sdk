package com.banxa.nativepaymentssdk.data.model

data class ErrorResponse(
    val message: String? = null,
    val errors: Map<String, List<String>>? = null
)