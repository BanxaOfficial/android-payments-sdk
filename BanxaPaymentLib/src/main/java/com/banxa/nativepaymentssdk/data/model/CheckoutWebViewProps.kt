package com.banxa.nativepaymentssdk.data.model

data class CheckoutWebViewProps(
    val checkoutUrl: String,
    val visible: Boolean,
    val onClose: () -> Unit,
    val onNavigationStateChange: ((Any) -> Unit)? = null,
    val onSuccess: ((String) -> Unit)? = null,
    val onFailure: ((String) -> Unit)? = null,
    val onCancelled: ((String) -> Unit)? = null,
    val returnUrlOnSuccess: String? = null,
    val returnUrlOnFailure: String? = null,
    val returnUrlOnCancelled: String? = null
)
