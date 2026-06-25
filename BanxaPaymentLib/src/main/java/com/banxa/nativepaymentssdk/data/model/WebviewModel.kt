package com.banxa.nativepaymentssdk.data.model

data class CheckoutWebViewExtras(
    val returnUrlOnSuccess: String? = null,
    val returnUrlOnFailure: String? = null,
    val returnUrlOnCancelled: String? = null,
    val onClose: (() -> Unit)? = null,
    val onSuccess: ((url: String) -> Unit)? = null,
    val onFailure: ((url: String) -> Unit)? = null,
    val onCancelled: ((url: String) -> Unit)? = null
)

data class CheckoutWebViewState(
    val checkoutUrl: String,
    val visible: Boolean = true,
    val extras: CheckoutWebViewExtras? = null
)