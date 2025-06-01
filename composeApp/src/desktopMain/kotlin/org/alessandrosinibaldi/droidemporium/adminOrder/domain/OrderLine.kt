package org.alessandrosinibaldi.droidemporium.adminOrder.domain

import kotlinx.serialization.Serializable

@Serializable
data class OrderLine(
    val id: String? = null,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val priceAtPurchase: Double

)
