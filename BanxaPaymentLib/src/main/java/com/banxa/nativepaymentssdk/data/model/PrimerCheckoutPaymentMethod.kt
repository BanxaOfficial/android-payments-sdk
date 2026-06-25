package com.banxa.nativepaymentssdk.data.model

/**
 * Which Primer native checkout UI to present after creating an order with `nativeToken`.
 * - `card` — card / payment-method sheet (`showPaymentMethod`)
 * - `applePay` / `googlePay` — headless native wallet flows
 * - `universal` — full Primer Universal Checkout (all methods)
 */
enum class PrimerCheckoutPaymentMethod {
    CARD,
    APPLE_PAY,
    GOOGLE_PAY,
    UNIVERSAL
}

//export type PrimerCheckoutPaymentMethod = 'card' | 'applePay' | 'googlePay' | 'universal'; kotlin version