package com.banxa.nativepaymentssdk.data.model

import com.google.gson.annotations.SerializedName

/** Query parameters for GET /{partner}/v2/quotes/{orderType}. */
data class QuoteRequest(
    val fiat: String,
    val crypto: String,
    val blockchain: String,
    val paymentMethodId: String,
    val fiatAmount: String? = null,
    val cryptoAmount: String? = null,
    val externalCustomerId: String? = null,
    val ipAddress: String? = null,
    val discountCode: String? = null
)

/** Pricing returned by GET /{partner}/v2/quotes/{orderType}. */
data class Quote(
    val paymentMethodId: String,
    val cryptoAmount: String,
    val fiatAmount: String,
    val processingFee: String? = null,
    val networkFee: String? = null,
    val discount: QuoteDiscount? = null
)

data class QuoteDiscount(
    val originalQuote: OriginalQuote? = null,
    val discountCode: String? = null
)

data class OriginalQuote(
    val originalCryptoAmount: String? = null,
    /** Banxa API field is misspelled as `orginalNetworkFee`. */
    @SerializedName("orginalNetworkFee")
    val originalNetworkFee: String? = null,
    val originalProcessingFee: String? = null,
    val originalFiatAmount: String? = null
)
