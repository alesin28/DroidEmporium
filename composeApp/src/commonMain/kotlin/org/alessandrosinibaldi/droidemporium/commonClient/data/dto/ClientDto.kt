package org.alessandrosinibaldi.droidemporium.commonClient.data.dto

import kotlinx.serialization.Serializable
import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client

@Serializable
data class ClientDto(
    val displayName: String = "",
    val email: String = "",
    val phoneNumber: String? = null,
)

fun ClientDto.toDomain(id: String): Client {
    return Client(
        id = id,
        displayName = this.displayName,
        email = this.email,
        phoneNumber = this.phoneNumber,
    )
}

