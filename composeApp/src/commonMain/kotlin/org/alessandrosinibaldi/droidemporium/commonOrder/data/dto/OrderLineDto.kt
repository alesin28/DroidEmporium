package org.alessandrosinibaldi.droidemporium.commonOrder.data.dto

import kotlinx.serialization.Serializable
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.OrderLine

@Serializable
data class OrderLineDto(
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val priceAtPurchase: Double = 0.0
)

fun OrderLineDto.toDomain(id: String): OrderLine {
    return OrderLine(
        id = id,
        productId = this.productId,
        productName = this.productName,
        quantity = this.quantity,
        priceAtPurchase = this.priceAtPurchase
    )
}