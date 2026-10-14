package com.banxa.nativepaymentssdk.ui

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.banxa.nativepaymentssdk.data.model.BanxaKycError
import com.banxa.nativepaymentssdk.data.model.BanxaKycResult
import com.banxa.nativepaymentssdk.data.model.KycSession
import com.banxa.nativepaymentssdk.kyc.HostedKycEvent
import com.banxa.nativepaymentssdk.kyc.HostedKycSessionMonitor
import com.valuelabsworkspace.feature_main_entry.presentation.components.ModalBottomSheetWrapper
import java.net.URI

private val CAPTURE_PERMISSIONS = listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

/**
 * Shows the Banxa hosted KYC flow for one customer in a bottom sheet.
 *
 * @param session
 * @param returnUrl
 * @param allowedOrigins
 * @param banxaDidCompleteKyc
 * @param banxaKycDidFail
 * @param banxaDidDismissKyc
 * @param onMessageRejected
 * @return
 */
@Composable
fun StartKyc(
    session: KycSession,
    returnUrl: String? = null,
    allowedOrigins: List<String> = emptyList(),
    banxaDidCompleteKyc: (BanxaKycResult) -> Unit = {},
    banxaKycDidFail: (BanxaKycError) -> Unit = {},
    banxaDidDismissKyc: () -> Unit = {},
    onMessageRejected: (HostedKycEvent.Rejected) -> Unit = {}
) {
    val context = LocalContext.current
    val currentDidComplete by rememberUpdatedState(banxaDidCompleteKyc)
    val currentDidFail by rememberUpdatedState(banxaKycDidFail)

    val monitor = remember(session, returnUrl, allowedOrigins) {
        HostedKycSessionMonitor(session, returnUrl, allowedOrigins)
    }
    val preflightError = remember(monitor) {
        when {
            monitor.isExpired -> BanxaKycError.SessionExpired
            !isHttps(session.redirectUrl) -> BanxaKycError.InvalidSessionUrl(session.redirectUrl)
            else -> undeclaredPermissions(context, CAPTURE_PERMISSIONS)
                .takeIf { it.isNotEmpty() }
                ?.let { BanxaKycError.MissingPermissionDeclaration(it) }
        }
    }
    var isOpen by remember(monitor) { mutableStateOf(preflightError == null) }

    LaunchedEffect(preflightError) {
        preflightError?.let { currentDidFail(it) }
    }

    if (!isOpen) return

    ModalBottomSheetWrapper(
        hideClick = {
            isOpen = false
            banxaDidDismissKyc()
        }
    ) {
        HostedKycWebViewScreen(
            url = session.redirectUrl,
            monitor = monitor,
            onEvent = { event ->
                when (event) {
                    is HostedKycEvent.Complete -> {
                        isOpen = false
                        currentDidComplete(event.result)
                    }
                    is HostedKycEvent.Error -> currentDidFail(BanxaKycError.FlowError(event.code, event.message))
                    is HostedKycEvent.Rejected -> onMessageRejected(event)
                    HostedKycEvent.Loading, HostedKycEvent.Ready -> Unit
                }
            },
            onPermissionDenied = { currentDidFail(BanxaKycError.PermissionDenied) },
            onRendererGone = {
                isOpen = false
                currentDidFail(BanxaKycError.FlowError("renderer_gone", "The verification page stopped unexpectedly."))
            }
        )
    }
}

/**
 * Returns whether [url] uses https.
 *
 * @param url
 * @return
 */
private fun isHttps(url: String): Boolean = try {
    URI(url).scheme.equals("https", ignoreCase = true)
} catch (e: Exception) {
    false
}
