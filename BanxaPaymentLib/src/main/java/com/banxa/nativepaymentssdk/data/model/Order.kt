package com.banxa.nativepaymentssdk.data.model

data class Order(
    val id: String,
    val externalCustomerId: String? = null,
    val externalOrderId: String? = null,
    val orderType: OrderType? = null,
    val fiat: String? = null,
    val fiatAmount: String? = null,
    val crypto: String? = null,
    val cryptoAmount: String? = null,
    val walletAddress: String? = null,
    val walletAddressTag: String? = null,
    val paymentMethodId: Int? = null,
    val paymentMethodType: String? = null,
    val status: OrderStatus? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val checkoutUrl: String? = null,
    val nativeToken: String? = null,
    val blockchain: String? = null
)

enum class OrderStatus {
    PENDING_PAYMENT,
    WAITING_PAYMENT,
    PAYMENT_RECEIVED,
    IN_PROGRESS,
    COIN_TRANSFERRED,
    CANCELLED,
    DECLINED,
    EXPIRED,
    COMPLETE,
    REFUNDED
}

/*

enum class OrderStatus {
    @SerializedName("pendingPayment")
    PENDING_PAYMENT,

    @SerializedName("waitingPayment")
    WAITING_PAYMENT,

    @SerializedName("paymentReceived")
    PAYMENT_RECEIVED,

    @SerializedName("inProgress")
    IN_PROGRESS,

    @SerializedName("coinTransferred")
    COIN_TRANSFERRED,

    @SerializedName("cancelled")
    CANCELLED,

    @SerializedName("declined")
    DECLINED,

    @SerializedName("expired")
    EXPIRED,

    @SerializedName("complete")
    COMPLETE,

    @SerializedName("refunded")
    REFUNDED
}
*/


enum class OrderType {
    BUY,
    SELL
}
/*
Use annotations if your API sends lowercase values ("buy", "sell"), otherwise parsing will fail.
@Serializable
enum class OrderType {
    @SerialName("buy")
    BUY,
    @SerialName("sell")
    SELL
}*/



/** Order as returned by Banxa API (camelCase) */
/*
export interface Order {
    id: string;
    externalCustomerId?: string;
    externalOrderId?: string;
    orderType?: 'BUY' | 'SELL';
    fiat?: string;
    fiatAmount?: string;
    crypto?: string;
    cryptoAmount?: string;
    walletAddress?: string;
    walletAddressTag?: string;
    paymentMethodId?: number;
    paymentMethodType?: string;
    status?: OrderStatus;
    createdAt?: string;
    updatedAt?: string;
    checkoutUrl?: string;
    nativeToken?: string;
    blockchain?: string;
}*/

//export type OrderStatus =
//| 'pendingPayment'
//| 'waitingPayment'
//| 'paymentReceived'
//| 'inProgress'
//| 'coinTransferred'
//| 'cancelled'
//| 'declined'
//| 'expired'
//| 'complete'
//| 'refunded';

//export type OrderType = 'buy' | 'sell';
