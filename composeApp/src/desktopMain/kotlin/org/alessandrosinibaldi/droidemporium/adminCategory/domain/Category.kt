package org.alessandrosinibaldi.droidemporium.adminCategory.domain

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String? = null,
    val name: String
)