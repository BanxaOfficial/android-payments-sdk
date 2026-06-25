package com.banxa.nativepaymentssdk.data.model


// Order API Types
/** Request body for POST /buy. Either fiatAmount or cryptoAmount must be provided. */

data class CreateBuyOrderRequest(
    val externalCustomerId: String = "",
    val fiat: String = "",
    val crypto: String = "",
    val fiatAmount: String = "",
    val cryptoAmount: String? = null,
    val walletAddress: String = "",
    val walletAddressTag: String? = null,
    val redirectUrl: String = "",
    val paymentMethodId: String = "",
    val blockchain: String? = null,
    val metadata: String? = null,
    val externalOrderId: String? = null,
    val subPartnerId: String? = null,
    val discountCode: String? = null,
    val email: String = ""
)

/* Type Script
export interface CreateBuyOrderRequest {
    externalCustomerId: string;
    fiat: string;
    crypto: string;
    fiatAmount?: string;
    cryptoAmount?: string;
    walletAddress: string;
    walletAddressTag?: string;
    redirectUrl: string;
    paymentMethodId?: string;
    blockchain?: string;
    metadata?: string;
    externalOrderId?: string;
    subPartnerId?: string;
    discountCode?: string;
    email?: string;
}*/
