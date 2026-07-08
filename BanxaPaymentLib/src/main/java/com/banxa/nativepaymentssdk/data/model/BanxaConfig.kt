package com.banxa.nativepaymentssdk.data.model

data class BanxaConfig(
    val apiKey: String,
    val partner: String,
    val environment: Environment? = null,
    val baseUrl: String? = null,
    val primerCallbacks: PrimerCallbacks? = null,
    val primerSettings: PrimerSettings? = null
)

enum class Environment {
    SANDBOX,
    PRODUCTION,
    LOCAL,
    PREPROD
}

data class PrimerSettings(
    val uiOptions: Any? = null,
    val paymentMethodOptions: Any? = null
)

data class PrimerError(
    val description: String? = null
)