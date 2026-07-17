package com.banxa.nativepaymentssdk.util

import android.app.Activity
import com.banxa.nativepaymentssdk.data.api.MySdk
import com.banxa.nativepaymentssdk.data.api.MySdk.getConfig
import com.banxa.nativepaymentssdk.data.model.CreateOrderRequest
import com.banxa.nativepaymentssdk.data.model.PrimerCallbacks

object CallbackManager {
    fun getCallbacks(): PrimerCallbacks? {
        return MySdk.getConfig().primerCallbacks
    }

    fun startCheckout(activity: Activity, createBuyOrderRequest: CreateOrderRequest) {

        val sdkConfig = getConfig() // ensures init is called

        /*val intent = Intent(activity, CheckoutActivity::class.java).apply {
            putExtra("checkout_config", checkoutConfig)
        }*/

        //activity.startActivity(intent)
    }
}