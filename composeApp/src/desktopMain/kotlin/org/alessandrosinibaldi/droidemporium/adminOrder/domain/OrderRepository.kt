package org.alessandrosinibaldi.droidemporium.adminOrder.domain

import kotlinx.coroutines.flow.Flow
import org.alessandrosinibaldi.droidemporium.core.domain.Result

interface OrderRepository {

    fun searchOrders(query: String): Flow<Result<List<Order>>>

    suspend fun getOrderById(id: String): Result<Order?>

}