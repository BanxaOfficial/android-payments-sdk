package com.banxa.nativepaymentssdk.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.banxa.nativepaymentssdk.core.PaymentSdkConfig

object DefaultThemes {
    var lightColors: ColorScheme = lightColorScheme(
        primary = Color(0xFF4CAF50),
        background = Color.White,
        onBackground = Color.Black
    )

    var darkColors: ColorScheme = darkColorScheme(
        primary = Color(0xFF4CAF50),
        background = Color(0xFF121212),
        onBackground = Color.White
    )
}
object PaymentSdkManager {

    var config: PaymentSdkConfig = PaymentSdkConfig(
        lightColors = DefaultThemes.lightColors,
        darkColors = DefaultThemes.darkColors
    )

    fun initialize(
        sdkConfig: PaymentSdkConfig
    ) {
        config = sdkConfig
    }
}