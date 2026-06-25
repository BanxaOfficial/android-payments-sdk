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

/*   Type Script
    export interface CheckoutWebViewProps {
        checkoutUrl: string;
        visible: boolean;
        onClose: () => void;
        onNavigationStateChange?: (navState: any) => void;
        onSuccess?: (url: string) => void;
        onFailure?: (url: string) => void;
        onCancelled?: (url: string) => void;
        returnUrlOnSuccess?: string;
        returnUrlOnFailure?: string;
        returnUrlOnCancelled?: string;
    }
*/
