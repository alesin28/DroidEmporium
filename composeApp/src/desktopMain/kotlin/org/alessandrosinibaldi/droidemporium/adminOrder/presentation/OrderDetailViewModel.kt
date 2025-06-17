package org.alessandrosinibaldi.droidemporium.adminOrder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.adminClient.domain.AdminClientRepository
import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.OrderRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

class OrderDetailViewModel(
    private val orderRepository: OrderRepository,
    private val adminClientRepository: AdminClientRepository,
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

            val orderResult = orderRepository.getOrderById(id)

            val fetchedOrder = when (orderResult) {
                is Result.Success -> orderResult.data
                is Result.Failure -> {
                    println("Error fetching order detail: ${orderResult.exception.message}")
                    null
                }
            }
            _order.value = fetchedOrder

            if (fetchedOrder != null) {
                val clientResult = adminClientRepository.getClientById(fetchedOrder.clientId)

                _client.value = when (clientResult) {
                    is Result.Success -> clientResult.data
                    is Result.Failure -> {
                        println("Error fetching client for order detail: ${clientResult.exception.message}")
                        null
                    }
                }
            } else {
                _client.value = null
            }
        }
    }
}