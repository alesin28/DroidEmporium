package org.alessandrosinibaldi.droidemporium.adminOrder.data.dto

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.OrderLine

@Serializable
data class OrderDto(
    val clientId: String = "",
    val orderDate: Timestamp? = null,
    val totalAmount: Double = 0.0
)

fun OrderDto.toDomain(id: String, lines: List<OrderLine>): Order {
    return Order(
        id = id,
        clientId = this.clientId,
        orderDate = this.orderDate?.toInstant() ?: Instant.DISTANT_PAST,
        totalAmount = this.totalAmount,
        lines = lines
    )
}

private fun Timestamp.toInstant(): Instant {
    return Instant.fromEpochSeconds(this.seconds, this.nanoseconds)
}
