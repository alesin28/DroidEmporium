package org.alessandrosinibaldi.droidemporium.adminOrder.domain


data class OrderLine(
    val id: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val priceAtPurchase: Double
) {
    val lineTotal: Double
        get() = quantity * priceAtPurchase
}