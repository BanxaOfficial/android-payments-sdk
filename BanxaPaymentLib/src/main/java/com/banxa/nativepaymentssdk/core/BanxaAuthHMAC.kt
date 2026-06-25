package com.banxa.nativepaymentssdk.core

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class BanxaAuthHMAC {

    companion object {
        private const val KEY = "[YOUR_API_KEY]"
        private const val SECRET = "[YOUR_API_SECRET]"
    }

    @Throws(Exception::class)
    fun generateHmac(method: String, path: String, payload: String?): String {
        val nonce = System.currentTimeMillis().toString()

        var data = "$method\n$path\n$nonce"
        if (payload != null) {
            data += "\n$payload"
        }

        val signingKey = SecretKeySpec(SECRET.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(signingKey)

        val hashBytes = mac.doFinal(data.toByteArray())

        val hex = hashBytes.joinToString("") { "%02x".format(it) }

        return "$KEY:$hex:$nonce"
    }
}