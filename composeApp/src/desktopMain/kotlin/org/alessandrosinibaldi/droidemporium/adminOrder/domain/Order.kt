package org.alessandrosinibaldi.droidemporium.adminOrder.domain

import kotlinx.datetime.Instant

data class Order(
    val id: String,
    val clientId: String,
    val orderDate: Instant,
    val totalAmount: Double,
    val lines: List<OrderLine> = emptyList()
)