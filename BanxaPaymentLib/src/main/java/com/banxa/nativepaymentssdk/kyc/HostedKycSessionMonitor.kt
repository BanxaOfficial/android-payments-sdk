package com.banxa.nativepaymentssdk.kyc

import com.banxa.nativepaymentssdk.data.model.BanxaKycResult
import com.banxa.nativepaymentssdk.data.model.KycCompletionChannel
import com.banxa.nativepaymentssdk.data.model.KycSession
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import java.net.URI
import java.net.URISyntaxException

/** Message types the hosted flow posts. Shared with `space` and the JS, RN and iOS SDKs. */
object BanxaKycMessages {
    const val PREFIX = "banxa:"
    const val LOADING = "banxa:loading"
    const val READY = "banxa:ready"
    const val COMPLETE = "banxa:kyc-complete"
    const val ERROR = "banxa:error"
}

sealed interface HostedKycEvent {
    data class Complete(val result: BanxaKycResult) : HostedKycEvent
    data class Error(val code: String?, val message: String) : HostedKycEvent
    data object Loading : HostedKycEvent
    data object Ready : HostedKycEvent

    /** A `banxa:*` message that arrived but wasn't trusted. Reported so a broken integration has something to go on. */
    data class Rejected(val reason: RejectionReason, val type: String, val origin: String) : HostedKycEvent
}

enum class RejectionReason {
    ORIGIN,
    FRAME
}

sealed interface NavigationDecision {
    data object Allow : NavigationDecision

    /** Cancel the navigation. [completion] is null when the session had already completed. */
    data class Intercept(val completion: HostedKycEvent.Complete?) : NavigationDecision
}

/**
 * Decides what the hosted KYC WebView should do with each message, navigation and camera request.
 * Kept free of Android types so all of it runs as a plain JVM unit test.
 */
class HostedKycSessionMonitor(
    private val session: KycSession,
    returnUrl: String?,
    allowedOrigins: List<String> = emptyList(),
    private val now: () -> Long = System::currentTimeMillis
) {
    private val returnUri: URI? = returnUrl?.let(::parseUri)
    private var completed = false

    val acceptedOrigins: List<String> =
        (listOf(session.redirectUrl) + allowedOrigins)
            .filter { it.isNotBlank() }
            .mapNotNull(::originOf)
            .distinct()

    val isExpired: Boolean
        get() = session.expirationMillis?.let { it <= now() } ?: false

    /**
     * Converts a forwarded `postMessage` payload into an event, or returns null when it isn't a Banxa message.
     *
     * @param json
     * @param origin
     * @param isMainFrame
     * @return
     */
    fun handleMessage(json: String, origin: String, isMainFrame: Boolean): HostedKycEvent? {
        val data = parseObject(json) ?: return null
        val type = data.stringOrNull("type") ?: return null
        if (!type.startsWith(BanxaKycMessages.PREFIX)) return null

        if (!isMainFrame) return HostedKycEvent.Rejected(RejectionReason.FRAME, type, origin)
        if (originOf(origin) !in acceptedOrigins) return HostedKycEvent.Rejected(RejectionReason.ORIGIN, type, origin)

        return when (type) {
            BanxaKycMessages.COMPLETE -> completeOnce(data.stringOrNull("status") ?: "complete", KycCompletionChannel.MESSAGE)
            BanxaKycMessages.ERROR -> HostedKycEvent.Error(data.stringOrNull("code"), data.stringOrNull("message") ?: "")
            BanxaKycMessages.LOADING -> HostedKycEvent.Loading
            BanxaKycMessages.READY -> HostedKycEvent.Ready
            else -> null
        }
    }

    /**
     * Decides whether a navigation is the redirect to the return URL, completing the session when it is.
     *
     * @param url
     * @return
     */
    fun handleNavigation(url: String): NavigationDecision {
        if (!matchesReturnUrl(url)) return NavigationDecision.Allow

        return NavigationDecision.Intercept(completeOnce("complete", KycCompletionChannel.RETURN_URL))
    }

    /**
     * Returns whether camera access may be granted while the main frame is on [mainFrameUrl].
     *
     * @param mainFrameUrl
     * @return
     */
    fun shouldGrantMediaCapture(mainFrameUrl: String?): Boolean {
        val origin = mainFrameUrl?.let(::originOf) ?: return false

        return origin in acceptedOrigins
    }

    /**
     * Completes the session through [channel], or returns null when it has already completed.
     *
     * @param status
     * @param channel
     * @return
     */
    private fun completeOnce(status: String, channel: KycCompletionChannel): HostedKycEvent.Complete? {
        if (completed) return null
        completed = true

        return HostedKycEvent.Complete(BanxaKycResult(status, channel))
    }

    /**
     * Returns whether [url] points at the return URL, ignoring its query string.
     *
     * @param url
     * @return
     */
    private fun matchesReturnUrl(url: String): Boolean {
        val target = returnUri ?: return false
        val candidate = parseUri(url) ?: return false

        if (!candidate.scheme.equals(target.scheme, ignoreCase = true)) return false

        // An opaque deep link such as `myapp:done` has no authority or path to compare.
        if (target.isOpaque || candidate.isOpaque) {
            return candidate.rawSchemeSpecificPart.substringBefore('?') ==
                target.rawSchemeSpecificPart.substringBefore('?')
        }

        if (!(candidate.rawAuthority ?: "").equals(target.rawAuthority ?: "", ignoreCase = true)) return false

        val targetPath = (target.rawPath ?: "").trimEnd('/')
        val candidatePath = candidate.rawPath ?: ""

        return candidatePath.trimEnd('/') == targetPath || candidatePath.startsWith("$targetPath/")
    }
}

private val gson = Gson()

/**
 * Parses a JSON string into an object, or returns null when it isn't one.
 *
 * @param json
 * @return
 */
private fun parseObject(json: String): JsonObject? {
    return try {
        gson.fromJson(json, JsonElement::class.java)?.takeIf { it.isJsonObject }?.asJsonObject
    } catch (e: JsonParseException) {
        null
    }
}

/**
 * Returns the value at [key] as a string, or null when it is missing or not a primitive.
 *
 * @param key
 * @return
 */
private fun JsonObject.stringOrNull(key: String): String? {
    val element = get(key) ?: return null

    return if (element.isJsonPrimitive) element.asString else null
}

/**
 * Parses a URI that has a scheme, or returns null.
 *
 * @param value
 * @return
 */
private fun parseUri(value: String): URI? {
    return try {
        URI(value.trim()).takeIf { it.scheme != null }
    } catch (e: URISyntaxException) {
        null
    }
}

/**
 * Returns the lowercased `scheme://host[:port]` origin of [url] without a default port, or null when it has none.
 *
 * @param url
 * @return
 */
internal fun originOf(url: String): String? {
    val uri = parseUri(url) ?: return null
    val scheme = uri.scheme.lowercase()
    val host = uri.host?.lowercase() ?: return null
    val defaultPort = when (scheme) {
        "https" -> 443
        "http" -> 80
        else -> -1
    }

    return if (uri.port == -1 || uri.port == defaultPort) "$scheme://$host" else "$scheme://$host:${uri.port}"
}
