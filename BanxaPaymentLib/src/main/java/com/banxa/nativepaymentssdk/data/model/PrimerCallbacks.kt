package com.banxa.nativepaymentssdk.data.model

interface PrimerCallbacks {
    fun onCheckoutComplete(checkoutData: Any?) {}
    fun onError(error: String) {}
    fun onDismiss() {}
}