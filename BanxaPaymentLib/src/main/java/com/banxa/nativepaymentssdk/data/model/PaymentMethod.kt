package com.banxa.nativepaymentssdk.data.model

data class PaymentMethod(
    val id: Int,
    val type: String,
    val name: String,
    val logo: String,
    val status: String,
    val supportedCurrencies: List<String>,
    val supportedCountries: List<String>
)

// Payment Methods API Types
/*
export interface PaymentMethod {
    id: number;
    type: string;
    name: string;
    logo: string;
    status: 'active' | 'inactive';
    supported_currencies: string[];
    supported_countries: string[];
}*/
