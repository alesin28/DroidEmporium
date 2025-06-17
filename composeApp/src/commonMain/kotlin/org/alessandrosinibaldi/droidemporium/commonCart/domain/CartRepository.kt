package org.alessandrosinibaldi.droidemporium.commonCart.domain

import kotlinx.coroutines.flow.Flow
import org.alessandrosinibaldi.droidemporium.core.domain.Result


interface CartRepository {
    fun getCart(clientId: String): Flow<Result<List<CartItem>>>
    suspend fun addToCart(clientId: String, productId: String, quantity: Int): Result<Unit>
    suspend fun removeFromCart(clientId: String, productId: String): Result<Unit>
    suspend fun updateQuantity(clientId: String, productId: String, newQuantity: Int): Result<Unit>
}