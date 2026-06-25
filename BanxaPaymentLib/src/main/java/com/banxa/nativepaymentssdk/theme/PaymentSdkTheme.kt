package com.banxa.nativepaymentssdk.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun PaymentSdkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colors = if (darkTheme) {
        PaymentSdkManager.config.darkColors
    } else {
        PaymentSdkManager.config.lightColors
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
