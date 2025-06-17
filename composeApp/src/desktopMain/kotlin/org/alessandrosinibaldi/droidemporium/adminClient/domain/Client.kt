package org.alessandrosinibaldi.droidemporium.adminClient.domain


data class Client(
    val id: String,
    val displayName: String,
    val email: String,
    val phoneNumber: String?,
    val isActive: Boolean
)