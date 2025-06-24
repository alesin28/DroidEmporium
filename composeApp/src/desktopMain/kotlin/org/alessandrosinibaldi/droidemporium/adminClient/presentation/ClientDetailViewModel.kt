package org.alessandrosinibaldi.droidemporium.adminClient.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.adminClient.domain.AdminClientRepository
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.AdminOrderRepository
import org.alessandrosinibaldi.droidemporium.adminReview.domain.AdminReviewRepository
import org.alessandrosinibaldi.droidemporium.commonAddress.domain.Address
import org.alessandrosinibaldi.droidemporium.commonAddress.domain.AddressRepository
import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.commonReview.domain.Review
import org.alessandrosinibaldi.droidemporium.core.domain.Result

class ClientDetailViewModel(
    private val adminClientRepository: AdminClientRepository,
    private val adminOrderRepository: AdminOrderRepository,
    private val adminReviewRepository: AdminReviewRepository,
    private val addressRepository: AddressRepository,
    private val clientId: String?
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _client = MutableStateFlow<Client?>(null)
    val client: StateFlow<Client?> = _client.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses.asStateFlow()

    init {
        if (clientId != null) {
            loadClientDetails(clientId)
        } else {
            _isLoading.value = false
        }
    }

    private fun loadClientDetails(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                coroutineScope {
                    val clientDeferred = async { adminClientRepository.getClientById(id) }
                    val ordersDeferred = async { adminOrderRepository.getOrdersByClient(id) }
                    val reviewsDeferred = async { adminReviewRepository.getReviewsByClient(id) }
                    val addressesDeferred = async { addressRepository.getAddressesForClient(id) }

                    _client.value = handleResult(clientDeferred.await(), "client details")
                    _orders.value =
                        handleResult(ordersDeferred.await(), "client orders") ?: emptyList()
                    _reviews.value =
                        handleResult(reviewsDeferred.await(), "client reviews") ?: emptyList()
                    _addresses.value =
                        handleResult(addressesDeferred.await(), "client addresses") ?: emptyList()
                }
            } catch (e: Exception) {
                println("An error occurred while loading client details: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun <T> handleResult(result: Result<T>, context: String): T? {
        return when (result) {
            is Result.Success -> result.data
            is Result.Failure -> {
                println("Error fetching $context: ${result.exception.message}")
                null
            }
        }
    }
}