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

## SDK Flow

```
MainActivity
      │
      ▼
Initialize Banxa Client
      │
      ▼
Create BanxaViewModel
      │
      ▼
Check Customer Eligibility
      │
      ├── Eligible
      │      │
      │      ▼
      │  Create Buy Order
      │      │
      │      ▼
      │ Launch Native Primer Checkout
      │
      └── Not Eligible / Error
             │
             ▼
      Display Validation or Error Message
```

---

## SDK Initialization

Create a `Banxa` instance using the Builder and initialize the SDK during application or activity startup.

```kotlin
val banxa = Banxa.Builder()
    .apiKey("<API_KEY>")
    .partner("<PARTNER_NAME>")
    .environment(Environment.SANDBOX)
    .build()

Banxa.initialize(banxa)
```
---

## ViewModel

`BanxaViewModel` acts as the entry point for all payment operations. It communicates with `BanxaRepository` to perform network requests and exposes UI states for the application.

Responsibilities:

* Validate customer eligibility
* Create Buy Order
* Manage loading, success, and error states
* Trigger Native Primer Checkout

---

## Eligibility Check

Before creating a Buy Order, the SDK validates whether the customer is eligible.

```kotlin
viewModel.checkEligibility(request)
```
## Step-1 : Configuration

```
fun getBanxa(): Banxa{
val primerTheme = PrimerTheme(
        lightColorTokens = object : LightColorTokens() {
            override val primerColorBrand: Color = Color(0xFF6C5CE7)
            override val primerColorTextPrimary: Color = Color(0xFFD32E2E)
            override val primerColorBackground: Color = Color(0xFF9CFFA1)
        },
    )
    return Banxa.Builder()
        .apiKey("<Your-API-Key>")
        .partner("<Your-Partner-Key>")
        .environment(Environment.SANDBOX)
        .primerTheme(primerTheme)
        .build()
}
```
## Step-2 : Initialize

```
var banxa = getBanxa()
Banxa.initialize(banxa)
```

## Step-3 : Create Order

```jetpack compose
CreateOrder(
            createBuyOrderRequest = CreateBuyOrderRequest(
                fiat = your-fiat,
                crypto = your-crypto,
                fiatAmount = your-fiatAmount,
                cryptoAmount = null,
                walletAddress = your-walletAddress,
                walletAddressTag = null,
                redirectUrl = your-redirectUrl,
                paymentMethodId = your-paymentMethodId,
                blockchain = null,
                metadata = null,
                externalOrderId = null,
                subPartnerId = null,
                discountCode = null,
                email = your-email
            ),
            onCheckoutComplete = {
                
            },
            onError = {
                
            },
            onDismiss = {
               
            },
        )
```

### Environments

| Environment   | Host                                  |
| ------------- | ------------------------------------- |
| `.sandbox`    | `https://api.banxa-sandbox.com`       |
| `.preprod`    | `https://api.banxa-preprod.com`       |
| `.production` | `https://api.banxa.com`               |

The effective API base URL is `<host>/<partnerID>/v2`.

## Models

### `CreateOrderRequest`

| Field                | Type     | Required | Notes                                              |
| -------------------- | -------- | -------- | -------------------------------------------------- |
| `paymentMethodID`    | String   | Yes      | Banxa payment method id (e.g. `"apple-pay"`).      |
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
