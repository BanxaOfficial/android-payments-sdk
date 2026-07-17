# BanxaPaymentSDK

A headless Android SDK that lets partners initiate Banxa fiat-to-crypto orders and complete the payment through the [Primer](https://primer.io/docs/sdk/android-checkout/v3.0.0-beta/installation) drop-in checkout. The SDK orchestrates the full flow — eligibility check, order creation, and Primer presentation — and forwards every relevant event back to the partner via a single callback.

## Features

- Headless API: configure once, start a payment with a single call.
- Automatic eligibility + create-order pipeline against the Banxa API.
- Built-in Primer drop-in presentation when a native token is available.
- Hosted-checkout fallback URL handed back to the partner when in-app payment is not possible.
- Strongly-typed request/response models and `APIError` cases.

## Requirements

- Android API level 24+
- Kotlin 2.0+ with the Compose Compiler Gradle plugin
- Jetpack Compose enabled in your project
- A Banxa partner account (`apiKey` + `partnerID`).

## Installation

### Using AAR File

Add banxalib.aar to libs folder:

```build.gradle
dependencies {
    implementation(files("libs/BanxaPayment.1.0.0"))
}
```

## Configuration & Initialize SDK

Configure the SDK once, ideally at app launch.

```Jetpack compose
Create a `Banxa` instance using the Builder and initialize the SDK.

```kotlin
val config = BanxaConfig.Builder()
    .apiKey("YOUR_BANXA_API_KEY")
    .partnerID("your-partner-slug")
    .environment(Environment.SANDBOX)
     //.primerTheme(primerTheme) //optional
    .build()

Banxa.initialize(config)
```

### Environments

| Environment   | Host                                  |
| ------------- | ------------------------------------- |
| `.sandbox`    | `https://api.banxa-sandbox.com`       |
| `.preprod`    | `https://api.banxa-preprod.com`       |
| `.production` | `https://api.banxa.com`               |

The effective API base URL is `<host>/<partnerID>/v2`.

## Starting a Payment

Call Jetpack Compose function for create Order:

```Jetpack Compose
StartPayment(
            createOrderRequest = CreateOrderRequest(
                fiat = your-fiat,
                crypto = your-crypto,
                fiatAmount = your-fiatAmount,
                walletAddress = your-walletAddress,
                redirectUrl = your-redirectUrl,
                paymentMethodId = your-paymentMethodId,
                email = your-email
            )
        )
```

## Handling Callbacks

Jetpack compose function lamda functions to receive both Banxa flow events and forwarded Primer drop-in callbacks. All methods have default no-op implementations — implement only what you need.

```Jetpack Compose
StartPayment(
            banxaDidReceiveCheckout = { result ->
                 // Success — from either the native in-app flow or the internal WebView.
        		 // Native path populates paymentId / orderId / status.
                 // WebView path populates rawQuery with the terminal success URL query string.
                 print("Paid:", result.paymentId ?? result.rawQuery ?? "-")
            },
            banxaDidFail = { error ->
                // Any failure: Banxa API / validation / network / decoding errors,
        		// in-app checkout errors (card decline, 3DS), or WebView failure URL.
            },
            banxaDidDismiss = { 
               // User closed the checkout UI without completing.
            }
)
```


## Models

### `CreateOrderRequest`

| Field                | Type     | Required | Notes                                              |
| -------------------- | -------- | -------- | -------------------------------------------------- |
| `paymentMethodID`    | String   | Yes      | Banxa payment method id (e.g. `"google-pay"`).      |
| `crypto`             | String   | Yes      | Crypto asset symbol (e.g. `"ETH"`).                |
| `fiat`               | String   | Yes      | Fiat currency code (e.g. `"EUR"`).                 |
| `fiatAmount`         | String   | Yes      | Fiat amount as a string.                           |
| `walletAddress`      | String   | Yes      | Destination wallet address.                        |
| `email`              | String   | Yes      | End-user's email address.                          |
| `redirectURL`        | String   | Yes      | URL Banxa redirects to after hosted checkout.      |
| `id`                 | String?  | No       | Partner-supplied order id.                         |
| `blockchain`         | String?  | No       | Explicit blockchain network.                       |
| `cryptoAmount`       | String?  | No       | Crypto amount when ordering by crypto value.       |
| `walletAddressTag`   | String?  | No       | Tag/memo for chains that require it.               |
| `subPartnerID`       | String?  | No       | Sub-partner identifier.                            |
| `metadata`           | String?  | No       | Opaque metadata string.                            |
| `externalCustomerID` | String?  | No       | Partner-side customer id.                          |
| `externalOrderID`    | String?  | No       | Partner-side order id.                             |
| `discountCode`       | String?  | No       | Promo / discount code.                             |

## Example

```Jetpack Compose
val primerTheme = PrimerTheme(
        lightColorTokens = object : LightColorTokens() {
            override val primerColorBrand: Color = Color(0xFF6C5CE7)
            override val primerColorTextPrimary: Color = Color(0xFFD32E2E)
            override val primerColorBackground: Color = Color(0xFF9CFFA1)
        },
    )
    val config = BanxaConfig.Builder()
    .apiKey("YOUR_BANXA_API_KEY")
    .partnerID("your-partner-slug")
    .environment(Environment.SANDBOX)
     //.primerTheme(primerTheme) //optional
    .build()

Banxa.initialize(config)
		
StartPayment(
            createOrderRequest = CreateOrderRequest(
                fiat = "EUR",
                crypto = "ETH",
                fiatAmount ="40",
                cryptoAmount = null,
                walletAddress = "ocx.........",
                redirectUrl = "demo://banxa-return",
                paymentMethodId = "debit-credit-card",
                email = "user@example.com"
            ),
            banxaDidReceiveCheckout = { result ->
                print("Payment complete:", result.paymentId ?? result.rawQuery ?? "-")
            },
            banxaDidFail = { error ->
                print("Payment failed:", error)
            },
            banxaDidDismiss = {
                 print("User dismissed checkout")
            },
        )
```

## Payment Methods & Supported Fiats

| Payment Method ID | Supported Fiats |
|-------------------|----------------|
| `payid-bank-transfer` | `AUD` |
| `pix` | `BRL` |
| `zar-bank-transfer` | `ZAR` |
| `pse` | `COP` |
| `khipu` | `CLP` |
| `debit-credit-card` | `AED`, `ARS`, `AUD`, `BRL`, `CAD`, `CHF`, `CZK`, `DKK`, `EUR`, `GBP`, `HKD`, `IDR`, `INR`, `JPY`, `KRW`, `MXN`, `MYR`, `NGN`, `NOK`, `NZD`, `PHP`, `PLN`, `QAR`, `RUB`, `SAR`, `SEK`, `SGD`, `THB`, `TRY`, `TWD`, `USD`, `VND`, `ZAR` |
| `apple-pay` | `AUD`, `EUR`, `GBP`, `USD` |
| `google-pay` | `AUD`, `EUR`, `USD` |
| `interac-bank-transfer` | `CAD` |
| `klarna-paynow` | `EUR` |
| `ideal-bank-transfer` | `AUD`, `EUR` |
| `sepa-bank-transfer` | `EUR` |
| `gbp-bank-transfer` | `GBP` |
| `spei` | `MXN` |
