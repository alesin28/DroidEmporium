package org.alessandrosinibaldi.droidemporium.commonProduct.domain

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val isActive: Boolean,
    val categoryId: String,
    val imageIds: List<String>,
    val defaultImageId: String
)