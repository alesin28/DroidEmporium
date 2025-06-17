package org.alessandrosinibaldi.droidemporium.adminClient.data.dto

import kotlinx.serialization.Serializable
import org.alessandrosinibaldi.droidemporium.adminClient.domain.Client

@Serializable
data class ClientDto(
    val displayName: String = "",
    val email: String = "",
    val phoneNumber: String? = null,
    val isActive: Boolean = true
)

fun ClientDto.toDomain(id: String): Client {
    return Client(
        id = id,
        displayName = this.displayName,
        email = this.email,
        phoneNumber = this.phoneNumber,
        isActive = this.isActive
    )
}

