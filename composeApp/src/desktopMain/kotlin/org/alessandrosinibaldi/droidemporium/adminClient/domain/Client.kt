package org.alessandrosinibaldi.droidemporium.adminClient.domain

import kotlinx.serialization.Serializable

@Serializable
data class Client(
    val id: String? = null,
    val displayName: String,
    val email: String,
    val phoneNumber: String? = null,
    val isActive: Boolean,
)