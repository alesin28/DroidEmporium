package org.alessandrosinibaldi.droidemporium.adminOrder.domain

import kotlinx.coroutines.flow.Flow
import org.alessandrosinibaldi.droidemporium.adminClient.domain.Client

interface OrderRepository {

    fun searchOrders(query: String? = null): Flow<Pair<List<Order>, List<Client>>>



}