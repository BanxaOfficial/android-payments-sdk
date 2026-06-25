package com.banxa.nativepaymentssdk.data.api

import com.banxa.nativepaymentssdk.data.model.BanxaConfig

object MySdk {

    private var config: BanxaConfig? = null

    fun init(config: BanxaConfig) {
        validateConfig(config)
        this.config = config
    }

    fun getConfig(): BanxaConfig {
        return config ?: throw IllegalStateException(
            "MySdk not initialized. Call MySdk.init() first."
        )
    }

    fun isInitialized(): Boolean = config != null

    private fun validateConfig(config: BanxaConfig) {
        require(config.apiKey.isNotBlank()) {
            "API Key cannot be empty"
        }
        require(config.partner.isNotBlank()) {
            "Partner cannot be empty"
        }
    }
}
