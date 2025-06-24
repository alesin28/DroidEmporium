package org.alessandrosinibaldi.droidemporium.commonAddress.data.dto

import kotlinx.serialization.Serializable
import org.alessandrosinibaldi.droidemporium.commonAddress.domain.Address

@Serializable
data class AddressDto(
    val label: String = "",
    val name: String = "",
    val surname: String = "",
    val street: String = "",
    val city: String = "",
    val province: String = "",
    val postalCode: String = "",
    val country: String = "",
    val isDefault: Boolean = false
)

fun AddressDto.toDomain(id: String): Address {
    return Address(
        id = id,
        label = this.label,
        name = this.name,
        surname = this.surname,
        street = this.street,
        city = this.city,
        province = this.province,
        postalCode = this.postalCode,
        country = this.country,
        isDefault = this.isDefault
    )
}