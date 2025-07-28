package org.alessandrosinibaldi.droidemporium.commonProduct.data.dto

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product


@Serializable
data class ProductDto(
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val isActive: Boolean = true,
    val categoryId: String = "",
    val imageIds: List<String> = emptyList(),
    val defaultImageId: String = "",
    val createdAt: Timestamp? = null
)

fun ProductDto.toDomain(id: String): Product {
    return Product(
        id = id,
        name = this.name,
        description = this.description,
        price = this.price,
        stock = this.stock,
        isActive = this.isActive,
        categoryId = this.categoryId,
        imageIds = this.imageIds,
        defaultImageId = this.defaultImageId,
        createdAt = this.createdAt?.toInstant() ?: Instant.DISTANT_PAST
    )


}

private fun Timestamp.toInstant(): Instant {
    return Instant.fromEpochSeconds(this.seconds, this.nanoseconds)
}