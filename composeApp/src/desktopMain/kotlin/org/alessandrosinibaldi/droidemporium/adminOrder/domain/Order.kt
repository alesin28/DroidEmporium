package org.alessandrosinibaldi.droidemporium.adminOrder.domain

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String? = null,
    val clientId: String,
    val orderDate: Timestamp,
    val totalAmount: Double,
    val lines: List<OrderLine> = emptyList()
)
