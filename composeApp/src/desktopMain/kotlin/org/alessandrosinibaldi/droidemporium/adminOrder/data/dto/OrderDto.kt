package org.alessandrosinibaldi.droidemporium.adminOrder.data.dto

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.alessandrosinibaldi.droidemporium.commonAddress.data.dto.AddressDto
import org.alessandrosinibaldi.droidemporium.commonAddress.domain.Address
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.OrderLine
import org.alessandrosinibaldi.droidemporium.commonAddress.data.dto.toDomain

@Serializable
data class OrderDto(
    val clientId: String = "",
    val clientName: String = "",
    val orderDate: Timestamp? = null,
    val totalAmount: Double = 0.0,
    val address: AddressDto? = null
)

fun OrderDto.toDomain(id: String, lines: List<OrderLine>): Order {
    val domainAddress = this.address?.toDomain(id = "")
        ?: Address(id="", label="N/A", name="N/A", surname="N/A", street="N/A", city="N/A", province="N/A", postalCode="N/A", country="N/A")

    return Order(
        id = id,
        clientId = this.clientId,
        clientName = this.clientName,
        orderDate = this.orderDate?.toInstant() ?: Instant.DISTANT_PAST,
        totalAmount = this.totalAmount,
        lines = lines,
        address = domainAddress
    )
}

private fun Timestamp.toInstant(): Instant {
    return Instant.fromEpochSeconds(this.seconds, this.nanoseconds)
}
