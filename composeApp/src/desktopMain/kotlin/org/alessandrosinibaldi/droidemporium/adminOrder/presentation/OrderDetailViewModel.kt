package org.alessandrosinibaldi.droidemporium.adminOrder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.adminClient.domain.Client
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.OrderRepository

class OrderDetailViewModel(
    private val orderRepository: OrderRepository,
    private val orderId: String?
) : ViewModel() {
    private val _order = MutableStateFlow<Order?>(null)
    val order = _order.asStateFlow()

    private val _client = MutableStateFlow<Client?>(null)
    val client = _client.asStateFlow()

    init {
        if (orderId != null) {
            loadOrder(orderId)
        }
    }

    private fun loadOrder(id: String) {
        viewModelScope.launch {
            val orderAndClient = orderRepository.getOrderById(id)
            if (orderAndClient != null) {
                _order.value = orderAndClient.first
                _client.value = orderAndClient.second
            }
        }
    }

}