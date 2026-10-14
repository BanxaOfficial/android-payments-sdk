package com.banxa.nativepaymentssdk.data.model

import com.google.gson.annotations.SerializedName
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * How thoroughly a hosted KYC session verifies the customer.
 * `EXPRESS` is accepted by the API but isn't binding yet, so the customer may still get the standard checks.
 */
enum class KycTier {
    @SerializedName("standard") STANDARD,
    @SerializedName("express") EXPRESS,
    @SerializedName("enhanced") ENHANCED
}

/**
 * Body for `POST /{partner}/v2/kyc/sessions`.
 * Leave a field null to omit it. Banxa treats a missing field differently from an empty one.
 */
data class CreateKycSessionRequest(
    val externalCustomerId: String,
    val tier: KycTier? = null,
    val country: String? = null,
    val returnUrl: String? = null
)

/**
 * A hosted KYC link for one customer.
 * Build it yourself when your backend creates the session, which keeps your API key off the device.
 */
data class KycSession(
    val redirectUrl: String,
    val expiresAt: String
) {
    /** When the link expires, in epoch millis, or null when [expiresAt] can't be read. */
    val expirationMillis: Long?
        get() = parseIsoTimestamp(expiresAt)
}

enum class KycCompletionChannel {
    MESSAGE,
    RETURN_URL
}

data class BanxaKycResult(
    val status: String,
    val channel: KycCompletionChannel
)

sealed class BanxaKycError(message: String) : Exception(message) {
    object SessionExpired : BanxaKycError("The verification session has expired. Create a new one to continue.")

    data class InvalidSessionUrl(val url: String) : BanxaKycError("The verification session URL must be https: $url")

    data class MissingPermissionDeclaration(val permissions: List<String>) :
        BanxaKycError("Declare these permissions in your app's AndroidManifest.xml: ${permissions.joinToString()}")

    object PermissionDenied : BanxaKycError("Camera access was denied, so identity documents can't be captured.")

    data class FlowError(val code: String?, val reason: String) :
        BanxaKycError(if (code == null) reason else "$code - $reason")
}

/**
 * Parses an ISO-8601 timestamp with an offset into epoch millis, or returns null when it can't be read.
 *
 * @param value
 * @return
 */
private fun parseIsoTimestamp(value: String): Long? {
    val withoutFraction = value.replace(Regex("""(T\d{2}:\d{2}:\d{2})\.\d+"""), "$1")

    return try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
            .apply { isLenient = false }
            .parse(withoutFraction)
            ?.time
    } catch (e: ParseException) {
        null
    }
}
