package org.alessandrosinibaldi.droidemporium.commonClient.domain

data class Client(
    val id: String,
    val displayName: String,
    val email: String,
    val phoneNumber: String?,
)