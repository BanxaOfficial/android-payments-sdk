package com.banxa.nativepaymentssdk.data.model

data class OrderEligibilityResponse(
    val eligible: Boolean,
    val paymentReady: Boolean? = null,
    val message: String? = null
)

/* Type Script
export interface OrderEligibilityResponse {
    eligible: boolean;
    paymentReady?: boolean;
    message?: string;
}*/
