package com.banxa.nativepaymentssdk.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.banxa.nativepaymentssdk.core.Banxa
import com.banxa.nativepaymentssdk.data.model.CreateBuyOrderRequest
import com.banxa.nativepaymentssdk.viewmodel.BanxaViewModel
import com.banxa.nativepaymentssdk.viewmodel.BanxaViewModelFactory
import com.banxa.nativepaymentssdk.viewmodel.BuyUiState
import com.banxa.nativepaymentssdk.viewmodel.EligibilityUiState
import com.valuelabsworkspace.feature_main_entry.presentation.components.ModalBottomSheetWrapper

@Composable
fun CreateOrder(
    createBuyOrderRequest: CreateBuyOrderRequest,
    onCheckoutComplete: () -> Unit = {},
    onError: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val banxa = Banxa.getInstance()
    val viewModel: BanxaViewModel =
        viewModel(factory = BanxaViewModelFactory(banxa.baseUrl, banxa.environment))
    val eligibilityUiState by viewModel.eligibilityUiState.collectAsState()
    val buyUiState by viewModel.buyUiState.collectAsState()

    LaunchedEffect(Unit) {
        if(banxa.apiKey.isEmpty() || banxa.partner.isEmpty()){
            onError.invoke("API Key or Partner Key should n't be empty.")
            return@LaunchedEffect
        }else{
            viewModel.checkEligibility(createBuyOrderRequest)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        /** Update EligibilityUiState [Start] **/
        when (eligibilityUiState) {
            EligibilityUiState.Idle -> {

            }

            EligibilityUiState.Loading -> {

            }

            is EligibilityUiState.Success -> {

            }

            is EligibilityUiState.NotEligible -> {

            }

            is EligibilityUiState.Error -> {
              val message = (eligibilityUiState as EligibilityUiState.Error)
                    .message
                onError.invoke(message)
                viewModel.resetEligibilityState()
            }
        }
        /** Update EligibilityUiState [End] **/

        /** Update BuyUiState [Start] **/
        when (buyUiState) {
            is BuyUiState.Idle -> {}
            is BuyUiState.Loading -> {

            }

            is BuyUiState.NoNativeToken -> {

            }

            is BuyUiState.Success -> {
                val nativeToken = (buyUiState as BuyUiState.Success).data.nativeToken
                val checkoutUrl = (buyUiState as BuyUiState.Success).data.checkoutUrl
                if (nativeToken.isEmpty().not()) {
                    PrimerCheckOutScreen(
                        nativeToken,
                        primerTheme = banxa.primerTheme,
                        onSuccessPrimerSDK = {
                            onCheckoutComplete.invoke()
                            viewModel.resetBuyState()
                        },
                        onFailurePrimerSDK = {
                            onError.invoke("Something went wrong from Primer SDK")
                            viewModel.resetBuyState()
                        },
                        onDismiss = {
                            onDismiss.invoke()
                            viewModel.resetBuyState()
                        })
                } else {
                    ModalBottomSheetWrapper(
                        hideClick = {
                            onDismiss.invoke()
                            viewModel.resetBuyState()
                        }
                    ) {
                        CheckoutWebViewScreen(
                            url = checkoutUrl,
                            onSuccess = {
                                onCheckoutComplete.invoke()
                                viewModel.resetBuyState()
                            },
                            onFailure = {
                                onError.invoke("Something went wrong from checkout webview")
                                viewModel.resetBuyState()
                            },
                            onDismiss = {
                                onDismiss.invoke()
                                viewModel.resetBuyState()
                            }
                        )
                    }
                }
            }

            is BuyUiState.Error -> {
                onError.invoke((buyUiState as BuyUiState.Error).message)
                viewModel.resetBuyState()
            }
        }
        /** Update BuyUiState [End] **/
    }
}


