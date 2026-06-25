package com.banxa.nativepaymentssdk.util

import com.banxa.nativepaymentssdk.data.model.BanxaConfig
import com.banxa.nativepaymentssdk.data.model.Environment

object EnvironmentConfig {

    fun getBaseUrl(config: BanxaConfig): String {
        return config.baseUrl ?: when (config.environment) {
            Environment.SANDBOX -> "https://sandbox-api.example.com"
            Environment.PRODUCTION -> "https://api.example.com"
            Environment.PREPROD -> "https://preprod-api.example.com"
            Environment.LOCAL -> "http://10.0.2.2:8080"
            null -> "https://sandbox-api.example.com"
        }
    }
}