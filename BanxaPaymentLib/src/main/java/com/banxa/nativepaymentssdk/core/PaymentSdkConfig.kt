package com.banxa.nativepaymentssdk.core

import androidx.compose.material3.ColorScheme
import com.banxa.nativepaymentssdk.theme.DefaultThemes


data class PaymentSdkConfig(
    val lightColors: ColorScheme = DefaultThemes.lightColors,
    val darkColors: ColorScheme = DefaultThemes.darkColors,
    val successTitle: String = "Payment is done successfully",
    val closeButtonText: String = "Close",
    val buyCoins: String = "Buy Coins",
    val sellCoins: String = "Sell Coins",
    val sell: String = "Sell",
    val spend: String = "Spend",
    val receive: String = "Receive",
    val walletAddress: String = "Your wallet address",
    val walletAddress_hint: String = "Enter your wallet address",
    val selectedBlockchain: String = "Selected blockchain",
    val paymentMethod: String = "Payment method",
    val summary: String = "Summary",
    val fullSummary: String = "Show Full Summary",
    val totalPayable: String = "Total Payable",
    val feesIncluded: String = "Fees Included",
    val continuebtn: String = "Create Order"

)