package com.banxa.nativepaymentssdk.data.model

import com.google.gson.annotations.SerializedName


data class EligibilityResponse(
    @SerializedName("message")val message: String? = null,
    @SerializedName("paymentReady")val paymentReady: Boolean = false,
    @SerializedName("kycRequirements")val requirements: List<Any> = emptyList()
)