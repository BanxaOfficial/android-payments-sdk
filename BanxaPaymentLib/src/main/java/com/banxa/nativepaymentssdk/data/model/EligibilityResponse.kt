package com.banxa.nativepaymentssdk.data.model

import com.google.gson.annotations.SerializedName


data class EligibilityResponse(
    @SerializedName("paymentReady")val paymentReady: Boolean,
    @SerializedName("kycRequirements")val requirements: List<Any> = emptyList()
)