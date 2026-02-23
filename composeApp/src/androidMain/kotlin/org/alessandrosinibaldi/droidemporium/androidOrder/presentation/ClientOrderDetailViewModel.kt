package org.alessandrosinibaldi.droidemporium.androidOrder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.androidAuth.domain.AuthRepository
import org.alessandrosinibaldi.droidemporium.androidOrder.domain.ClientOrderRepository
import org.alessandrosinibaldi.droidemporium.androidReview.domain.ClientReviewRepository
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.core.domain.Result

data class ClientOrderDetailUiState(
    val order: Order? = null,
    val reviewedProductIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class ClientOrderDetailViewModel(
    private val orderId: String,
    private val orderRepository: ClientOrderRepository,
    private val authRepository: AuthRepository,
    private val reviewRepository: ClientReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientOrderDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadOrder()
    }

    fun loadOrder() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = orderRepository.getOrderById(orderId)) {
                is Result.Success -> {
                    if (result.data != null) {
                        _uiState.update { it.copy(order = result.data, isLoading = false) }
                        checkReviewedProducts(result.data)
                    } else {
                        _uiState.update { it.copy(error = "Order not found", isLoading = false) }
                    }
                }

                is Result.Failure -> {
                    _uiState.update { it.copy(error = result.exception.message, isLoading = false) }
                }
            }
        }
    }

    private fun checkReviewedProducts(order: Order) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser().firstOrNull() ?: return@launch
            val reviewedIds = mutableSetOf<String>()

            order.lines.forEach { line ->
                val result = reviewRepository.hasUserReviewedProduct(user.id, line.productId)
                if (result is Result.Success && result.data) {
                    reviewedIds.add(line.productId)
                }
            }

            _uiState.update { it.copy(reviewedProductIds = reviewedIds) }
        }
    }

}