package org.alessandrosinibaldi.droidemporium.adminOrder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import org.alessandrosinibaldi.droidemporium.core.domain.Result
import kotlinx.coroutines.flow.map
import org.alessandrosinibaldi.droidemporium.adminClient.domain.AdminClientRepository
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.OrderRepository


class OrderListViewModel(
    private val orderRepository: OrderRepository,
    private val adminClientRepository: AdminClientRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()



    @OptIn(ExperimentalCoroutinesApi::class)
    val ordersWithClients: StateFlow<List<OrderWithClient>> = _searchQuery
        .flatMapLatest { query ->
            orderRepository.searchOrders(query)
        }
        .map { ordersResult ->
            when (ordersResult) {
                is Result.Success -> ordersResult.data
                is Result.Failure -> {
                    println("Error fetching orders: ${ordersResult.exception.message}")
                    emptyList()
                }
            }
        }
        .flatMapLatest { orders ->
            if (orders.isEmpty()) {
                flowOf(emptyList())
            } else {
                val clientIds = orders.map { it.clientId }.toSet()
                adminClientRepository.getClientsByIds(clientIds)
                    .map { clientsResult ->
                        val clients = when (clientsResult) {
                            is Result.Success -> clientsResult.data
                            is Result.Failure -> {
                                println("Error fetching clients: ${clientsResult.exception.message}")
                                emptyList() // On failure, use an empty list of clients
                            }
                        }
                        val clientMap = clients.associateBy { it.id }
                        orders.map { order ->
                            OrderWithClient(
                                order = order,
                                client = clientMap[order.clientId]
                            )
                        }
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}