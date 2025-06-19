package org.alessandrosinibaldi.droidemporium.commonAddress.domain

data class Address(
    val id: String,
    val label: String,
    val street: String,
    val city: String,
    val province: String,
    val postalCode: String,
    val country: String,
    val isDefault: Boolean = false
)