package org.alessandrosinibaldi.droidemporium.adminProduct.data.dto

import kotlinx.serialization.Serializable
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product


@Serializable
data class ProductDto(
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val isActive: Boolean = true,
    val categoryId: String = ""
)

fun ProductDto.toDomain(id: String): Product {
    return Product(
        id = id,
        name = this.name,
        description = this.description,
        price = this.price,
        stock = this.stock,
        isActive = this.isActive,
        categoryId = this.categoryId
    )
}