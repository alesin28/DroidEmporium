package org.alessandrosinibaldi.droidemporium.commonCart.domain

import kotlinx.datetime.Instant

data class CartItem(
    val productId: String,
    val quantity: Int,
    val productName: String,
    val productPrice: Double,
    val productImageUrl: String,
    val addedAt: Instant
)
