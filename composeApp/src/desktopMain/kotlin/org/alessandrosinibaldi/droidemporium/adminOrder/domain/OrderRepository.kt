package org.alessandrosinibaldi.droidemporium.adminOrder.domain

import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    fun searchOrders(query: String? = null): Flow<List<Order>>
}