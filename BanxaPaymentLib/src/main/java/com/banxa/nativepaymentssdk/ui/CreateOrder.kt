package com.banxa.nativepaymentssdk.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.banxa.nativepaymentssdk.core.Banxa
import com.banxa.nativepaymentssdk.core.getThemeColors
import com.banxa.nativepaymentssdk.core.toColorInt
import com.banxa.nativepaymentssdk.data.model.CreateBuyOrderRequest
import com.banxa.nativepaymentssdk.viewmodel.BanxaViewModel
import com.banxa.nativepaymentssdk.viewmodel.BanxaViewModelFactory
import com.banxa.nativepaymentssdk.viewmodel.BuyUiState
import com.banxa.nativepaymentssdk.viewmodel.EligibilityUiState
import com.valuelabsworkspace.feature_main_entry.presentation.components.ModalBottomSheetWrapper
import io.primer.checkout.PrimerTheme
import io.primer.checkout.internal.tokens.LightColorTokens

@Composable
fun CreateOrder(
    createBuyOrderRequest: CreateBuyOrderRequest,
    onCheckoutComplete: () -> Unit = {},
    onError: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val banxa = Banxa.getInstance()
    val theme = banxa.primerSettings?.uiOptions?.theme
    val appearanceMode = banxa.primerSettings?.uiOptions?.appearanceMode
    val colors = getThemeColors(banxa)
    val background = Color(colors?.background?.toColorInt() ?: Color.White.toArgb())
    val containerColor = Color(colors?.containerColor?.toColorInt() ?: Color.White.toArgb())
    val contrastingColor = Color(colors?.contrastingColor?.toColorInt() ?: Color.White.toArgb())
    val contrastingText = Color(colors?.contrastingText?.toColorInt() ?: Color.White.toArgb())
    val textColor = Color(colors?.text?.toColorInt() ?: Color.White.toArgb())

    val viewModel: BanxaViewModel =
        viewModel(factory = BanxaViewModelFactory())
    var apiStatus by remember { mutableStateOf("Checking Eligibility...") }
    val eligibilityUiState by viewModel.eligibilityUiState.collectAsState()
    val buyUiState by viewModel.buyUiState.collectAsState()

    val primerTheme = PrimerTheme(
        lightColorTokens = object : LightColorTokens() {
            override val primerColorBrand: Color = Color(0xFF6C5CE7)
            override val primerColorTextPrimary: Color = Color(0xFFD32E2E)
            override val primerColorBackground: Color = Color(0xFF9CFFA1)
        },
    )

    LaunchedEffect(Unit) {

        Log.i("BPS", "API Key : ${banxa.apiKey}")

        apiStatus = "Checking Eligibility..."
        viewModel.checkEligibility(createBuyOrderRequest)
    }
    /*PrimerCheckOutScreen("eyJhbGciOiJIUzI1NiIsImtpZCI6ImNsaWVudC10b2tlbi1zaWduaW5nLWtleSIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3ODE2MTQwNzgsImFjY2Vzc1Rva2VuIjoiZXlKaGJHY2lPaUpJVXpJMU5pSXNJbXRwWkNJNkltTnNhV1Z1ZEMxMGIydGxiaTF6YVdkdWFXNW5MV3RsZVNJc0luUjVjQ0k2SWtwWFZDSjkuZXlKcFlYUWlPakUzT0RFMU1qYzJOemdzSW5OMVlpSTZJams1TVdZeVpUWmlMV016T1RrdE5HSTVOQzA1WlRRd0xXRTROVEV6WXpFeVptUXdOQ0lzSW1wMGFTSTZJamt4TkdNNVl6UmxMVFEyT1RNdE5ESXdPUzFpWkRJMExXUmhNRFV5TnpNNE5UTmhOU0lzSW1WNGNDSTZNVGM0TVRZeE5EQTNPSDAuSVlRUTNFZkxJMnd6WFRCaXFscGNWTFY5ODJpWEVucm8xTzEtXzMzWDRHYyIsImFuYWx5dGljc1VybCI6Imh0dHBzOi8vYW5hbHl0aWNzLmFwaS5zYW5kYm94LmNvcmUucHJpbWVyLmlvL21peHBhbmVsIiwiYW5hbHl0aWNzVXJsVjIiOiJodHRwczovL2FuYWx5dGljcy5zYW5kYm94LmRhdGEucHJpbWVyLmlvL2NoZWNrb3V0L3RyYWNrIiwiaW50ZW50IjoiQ0hFQ0tPVVQiLCJjb25maWd1cmF0aW9uVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5wcmltZXIuaW8vY2xpZW50LXNkay9jb25maWd1cmF0aW9uIiwiY29yZVVybCI6Imh0dHBzOi8vYXBpLnNhbmRib3gucHJpbWVyLmlvIiwicGNpVXJsIjoiaHR0cHM6Ly9zZGsuYXBpLnNhbmRib3gucHJpbWVyLmlvIiwiZW52IjoiU0FOREJPWCIsInBheW1lbnRGbG93IjoiREVGQVVMVCJ9.vpw6e91aIRdQCV_Q4F-F3xhwZ_pc8KcHf8z27Kxza0E",primerTheme = primerTheme, onSuccessPrimerSDK = {
       // onCheckoutComplete.invoke()
    }, onFailurePrimerSDK = {
       // onError.invoke()
    },onDismiss = {
        onDismiss.invoke()
    })*/
    //CheckoutWebViewScreen()

    /*ModalBottomSheetWrapper(
        hideClick = {

        }
    ) {*/
        Column(modifier = Modifier.fillMaxWidth()) {
            /*Text(
                text = apiStatus, color = textColor
            )*/
            /** Update EligibilityUiState [Start] **/
            when (eligibilityUiState) {
                EligibilityUiState.Idle -> {

                }

                EligibilityUiState.Loading -> {
                    //CircularProgressIndicator()
                }

                is EligibilityUiState.Success -> {
                    apiStatus =
                        "Eligible Success : " + (eligibilityUiState as EligibilityUiState.Success)
                            .data
                            .toString()

                }

                is EligibilityUiState.NotEligible -> {
                    apiStatus = "Sorry, You are Not Eligible, the required Docs are " +  (eligibilityUiState as EligibilityUiState.NotEligible).listOfReq
                        .toString()

                }

                is EligibilityUiState.Error -> {
                    apiStatus = (eligibilityUiState as EligibilityUiState.Error)
                        .message
                }
            }
            /** Update EligibilityUiState [End] **/

            /** Update BuyUiState [Start] **/
            when (buyUiState) {
                is BuyUiState.Idle -> {}
                is BuyUiState.Loading -> {
                    //CircularProgressIndicator()
                    apiStatus = "Creating order..."
                }

                is BuyUiState.NoNativeToken -> {
                    apiStatus = "Sorry No Native Token Received. "
                }

                is BuyUiState.Success -> {
                    apiStatus = "Order Created successfully. " + (buyUiState as BuyUiState.Success)
                        .data
                        .toString()
                    val nativeToken = (buyUiState as BuyUiState.Success).data.nativeToken
                    val checkoutUrl = (buyUiState as BuyUiState.Success).data.checkoutUrl
                    if(nativeToken.isEmpty()){
                        PrimerCheckOutScreen(nativeToken,primerTheme = primerTheme, onSuccessPrimerSDK = {
                            onCheckoutComplete.invoke()
                        }, onFailurePrimerSDK = {
                            onError.invoke()
                        })
                    }else{
                        ModalBottomSheetWrapper(
                            hideClick = {

                            }
                        ) {
                            CheckoutWebViewScreen(url = checkoutUrl)
                        }
                    }
                }
                is BuyUiState.Error -> {
                    apiStatus = (buyUiState as BuyUiState.Error).message
                }
            }
            /** Update BuyUiState [End] **/
        }
    /*}*/
}


