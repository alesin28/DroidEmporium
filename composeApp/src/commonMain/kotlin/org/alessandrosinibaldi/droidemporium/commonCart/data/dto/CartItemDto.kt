package org.alessandrosinibaldi.droidemporium.commonCart.data.dto

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.alessandrosinibaldi.droidemporium.commonCart.domain.CartItem

@Serializable
data class CartItemDto(
    val quantity: Int = 0,
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productImageUrl: String = "",
    val addedAt: Timestamp? = null
)

fun CartItemDto.toDomain(productId: String): CartItem {
    return CartItem(
        productId = productId,
        quantity = this.quantity,
        productName = this.productName,
        productPrice = this.productPrice,
        productImageUrl = this.productImageUrl,
        addedAt = this.addedAt?.toInstant() ?: Instant.DISTANT_PAST
    )
}

private fun Timestamp.toInstant(): Instant {
    return Instant.fromEpochSeconds(this.seconds, this.nanoseconds)
}
