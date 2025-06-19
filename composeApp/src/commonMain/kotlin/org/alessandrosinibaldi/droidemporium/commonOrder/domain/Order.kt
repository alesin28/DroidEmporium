package org.alessandrosinibaldi.droidemporium.commonOrder.domain

import kotlinx.datetime.Instant
import org.alessandrosinibaldi.droidemporium.commonAddress.domain.Address

data class Order(
    val id: String,
    val clientId: String,
    val clientName: String,
    val orderDate: Instant,
    val totalAmount: Double,
    val lines: List<OrderLine> = emptyList(),
    val address: Address
)