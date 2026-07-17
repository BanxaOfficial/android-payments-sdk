package com.banxa.nativepaymentssdk.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.banxa.nativepaymentssdk.di.ServiceLocator
import io.primer.checkout.PrimerTheme

class BanxaConfig private constructor(
    val apiKey: String,
    val partner: String,
    val environment: Environment,
    val baseUrl: String? = null,
    val primerTheme: PrimerTheme? =null,
    val primerSettings: PrimerSettings?
) {

    companion object {
        @Volatile
        private var instance: BanxaConfig? = null

        fun initialize(banxa: BanxaConfig) {
            instance = banxa
        }

        fun getInstance(): BanxaConfig {
            return instance
                ?: throw IllegalStateException("Banxa is not initialized. Call initialize() first.")
        }
    }


    class Builder {
        private var apiKey: String = ""
        private var partnerID: String = ""
        private var primerTheme: PrimerTheme? = null
        private var environment: Environment = Environment.SANDBOX
        private var primerSettings: PrimerSettings? = null

        fun apiKey(apiKey: String) = apply { this.apiKey = apiKey }
        fun partnerID(partnerID: String) = apply { this.partnerID = partnerID }
        fun environment(environment: Environment) = apply { this.environment = environment }
        fun primerTheme(primerTheme: PrimerTheme?) = apply { this.primerTheme = primerTheme }
        fun primerSettings(settings: PrimerSettings) = apply { this.primerSettings = settings }

        fun build(): BanxaConfig {
            ServiceLocator.init()
            return BanxaConfig(apiKey= apiKey, partner = partnerID, environment = environment,primerTheme = primerTheme, primerSettings = primerSettings)
        }
    }
}



fun RGBA.toColorInt(): Int {
    return android.graphics.Color.argb(alpha, red, green, blue)
}


enum class Environment {
    SANDBOX,
    PRODUCTION,
    PREPROD,
    LOCAL
}

data class PrimerSettings(
    val uiOptions: UiOptions?
)

data class UiOptions(
    val appearanceMode: AppearanceMode = AppearanceMode.LIGHT,
    val theme: Theme?
)

enum class AppearanceMode {
    SYSTEM,
    LIGHT,
    DARK
}
data class Theme(
    val colors: Colors,
    val darkModeColors: Colors?
)

data class Colors(
    val mainColor: RGBA,
    val containerColor: RGBA,
    val contrastingColor: RGBA,
    val background: RGBA,
    val text: RGBA,
    val contrastingText: RGBA,
    val borders: RGBA,
    val disabled: RGBA,
    val error: RGBA
)

data class RGBA(
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Int
)



object BanxaSDK {
    private var instance: BanxaConfig? = null
    private var test: String = ""

    fun initialize(banxa: BanxaConfig) {
        instance = banxa
    }
    fun initializeTest(test1: String) {
        test = test1
    }

    fun get(): BanxaConfig {
        return instance ?: throw IllegalStateException("BanxaSDK not initialized")
    }
}

@Composable
fun getThemeColors(banxa: BanxaConfig): Colors? {
    val uiOptions = banxa.primerSettings?.uiOptions
    val theme = uiOptions?.theme

    val isSystemDark = isSystemInDarkTheme()

    return when (uiOptions?.appearanceMode) {

        AppearanceMode.DARK -> {
            theme?.darkModeColors ?: theme?.colors
        }

        AppearanceMode.LIGHT -> {
            theme?.colors
        }

        AppearanceMode.SYSTEM -> {
            if (isSystemDark) {
                theme?.darkModeColors ?: theme?.colors
            } else {
                theme?.colors
            }
        }
        else -> theme?.colors
    }
}



