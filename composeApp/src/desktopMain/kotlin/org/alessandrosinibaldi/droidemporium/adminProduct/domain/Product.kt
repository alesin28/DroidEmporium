package org.alessandrosinibaldi.droidemporium.adminProduct.domain

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String? = null,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val isActive: Boolean,
    val categoryId: String
)
