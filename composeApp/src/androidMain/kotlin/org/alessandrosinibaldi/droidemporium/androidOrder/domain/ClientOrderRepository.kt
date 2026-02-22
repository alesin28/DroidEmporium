package org.alessandrosinibaldi.droidemporium.androidOrder.domain

import org.alessandrosinibaldi.droidemporium.commonAddress.domain.Address
import org.alessandrosinibaldi.droidemporium.commonCart.domain.CartItem
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.OrderRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

interface ClientOrderRepository : OrderRepository {
    suspend fun placeOrder(
        clientId: String,
        clientName: String,
        cartItems: List<CartItem>,
        address: Address,
        totalAmount: Double
    ): Result<Unit>
}