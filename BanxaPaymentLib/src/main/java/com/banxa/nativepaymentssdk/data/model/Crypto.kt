package com.banxa.nativepaymentssdk.data.model

/** A cryptocurrency returned by GET /{partner}/v2/crypto/{orderType}. */
data class Crypto(
    val id: String,
    val description: String,
    val blockchains: List<Blockchain> = emptyList()
)

/** A blockchain network available for a [Crypto] asset. */
data class Blockchain(
    val id: String,
    val description: String,
    val isDefaultBlockchain: Boolean = false,
    val address: String? = null,
    val network: String? = null,
    val minimum: String? = null,
    /** Country code → restricted state/province codes (empty list = whole country). */
    val unsupportedCountries: Map<String, List<String>> = emptyMap()
)
