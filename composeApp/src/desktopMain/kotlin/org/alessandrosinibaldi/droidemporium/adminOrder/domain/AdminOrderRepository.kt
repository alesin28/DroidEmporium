package org.alessandrosinibaldi.droidemporium.adminOrder.domain

import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.OrderRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

interface AdminOrderRepository : OrderRepository {

    suspend fun getOrdersByProduct(clientId: String): Result<List<Order>>

    suspend fun getOrdersByClient(clientId: String): Result<List<Order>>

}