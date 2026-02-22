package org.alessandrosinibaldi.droidemporium.androidCart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.androidAuth.domain.AuthRepository
import org.alessandrosinibaldi.droidemporium.commonCart.domain.CartItem
import org.alessandrosinibaldi.droidemporium.commonCart.domain.CartRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val totalAmount: Double
        get() = items.sumOf { it.productPrice * it.quantity }
}

class CartViewModel(
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().collectLatest { user ->
                currentUserId = user?.id
                if (user != null) {
                    loadCart(user.id)
                } else {
                    _uiState.update { it.copy(isLoading = false, items = emptyList()) }
                }
            }
        }
    }

    private fun loadCart(clientId: String) {
        viewModelScope.launch {
            cartRepository.getCart(clientId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val sortedItems = result.data.sortedBy { it.addedAt }
                        _uiState.update {
                            it.copy(
                                items = sortedItems,
                                isLoading = false,
                                error = null
                            )
                        }
                    }

                    is Result.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.exception.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        val clientId = currentUserId ?: return
        viewModelScope.launch {
            if (newQuantity > 0) {
                cartRepository.updateQuantity(clientId, productId, newQuantity)
            } else {
                cartRepository.removeFromCart(clientId, productId)
            }
        }
    }

    fun removeItem(productId: String) {
        val clientId = currentUserId ?: return
        viewModelScope.launch {
            cartRepository.removeFromCart(clientId, productId)
        }
    }
}