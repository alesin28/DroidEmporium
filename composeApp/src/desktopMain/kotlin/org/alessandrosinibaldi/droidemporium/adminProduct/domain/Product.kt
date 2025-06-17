package org.alessandrosinibaldi.droidemporium.adminProduct.domain

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val isActive: Boolean,
    val categoryId: String
)
