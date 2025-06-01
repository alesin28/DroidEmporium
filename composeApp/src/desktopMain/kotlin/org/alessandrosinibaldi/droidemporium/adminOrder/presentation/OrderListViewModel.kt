package org.alessandrosinibaldi.droidemporium.adminOrder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import org.alessandrosinibaldi.droidemporium.adminClient.domain.Client
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.OrderRepository
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.Product

class OrderListViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<Pair<List<Order>, List<Client>>> = orderRepository.searchOrders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Pair(emptyList<Order>(), emptyList<Client>())
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

}