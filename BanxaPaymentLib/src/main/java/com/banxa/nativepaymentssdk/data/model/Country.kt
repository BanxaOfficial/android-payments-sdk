package com.banxa.nativepaymentssdk.data.model

/** A country returned by GET /{partner}/v2/countries. */
data class Country(
    val id: String,
    val description: String,
    val states: List<CountryState> = emptyList()
)

/** A state or province within a [Country]. */
data class CountryState(
    val id: String,
    val description: String
)
