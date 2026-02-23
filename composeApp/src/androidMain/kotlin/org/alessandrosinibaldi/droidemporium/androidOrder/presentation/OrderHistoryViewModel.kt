package org.alessandrosinibaldi.droidemporium.androidOrder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.androidAuth.domain.AuthRepository
import org.alessandrosinibaldi.droidemporium.androidOrder.domain.ClientOrderRepository
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.core.domain.Result

data class OrderHistoryUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class OrderHistoryViewModel(
    private val authRepository: AuthRepository,
    private val orderRepository: ClientOrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collectLatest { user ->
                if (user != null) {
                    orderRepository.getOrdersForClient(user.id).collect { result ->
                        when (result) {
                            is Result.Success -> {
                                _uiState.update {
                                    it.copy(orders = result.data, isLoading = false, error = null)
                                }
                            }

                            is Result.Failure -> {
                                _uiState.update {
                                    it.copy(isLoading = false, error = result.exception.message)
                                }
                            }
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "User not authenticated")
                    }
                }
            }
        }
    }
}