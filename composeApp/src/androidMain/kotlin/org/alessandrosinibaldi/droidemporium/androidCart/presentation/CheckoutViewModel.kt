package org.alessandrosinibaldi.droidemporium.androidCart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.androidAuth.domain.AuthRepository
import org.alessandrosinibaldi.droidemporium.androidOrder.domain.ClientOrderRepository
import org.alessandrosinibaldi.droidemporium.commonAddress.domain.Address
import org.alessandrosinibaldi.droidemporium.commonAddress.domain.AddressRepository
import org.alessandrosinibaldi.droidemporium.commonCart.domain.CartItem
import org.alessandrosinibaldi.droidemporium.commonCart.domain.CartRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

data class CheckoutUiState(
    val isLoading: Boolean = true,
    val isPlacingOrder: Boolean = false,
    val orderSuccess: Boolean = false,
    val cartItems: List<CartItem> = emptyList(),
    val addresses: List<Address> = emptyList(),
    val selectedAddress: Address? = null,
    val isAddressSelectorOpen: Boolean = false,
    val error: String? = null
) {
    val totalAmount: Double
        get() = cartItems.sumOf { it.productPrice * it.quantity }
}

class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val addressRepository: AddressRepository,
    private val authRepository: AuthRepository,
    private val orderRepository: ClientOrderRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    private var currentUserId: String? = null
    private var currentUserName: String? = null

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().collectLatest { user ->
                currentUserId = user?.id
                currentUserName = user?.displayName
                if (user != null) {
                    launch {
                        cartRepository.getCart(user.id).collect { result ->
                            if (result is Result.Success) {
                                _uiState.update {
                                    it.copy(
                                        cartItems = result.data,
                                        isLoading = false
                                    )
                                }
                            }
                        }
                    }
                    refreshAddresses()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "User not authenticated") }
                }
            }
        }
    }

    fun refreshAddresses() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            val result = addressRepository.getAddressesForClient(userId)
            if (result is Result.Success) {
                val addresses = result.data
                val defaultAddr = addresses.find { it.isDefault } ?: addresses.firstOrNull()

                _uiState.update { it.copy(addresses = addresses, selectedAddress = defaultAddr) }
            }
        }
    }

    fun selectAddress(address: Address) {
        _uiState.update { it.copy(selectedAddress = address, isAddressSelectorOpen = false) }
    }

    fun showAddressSelector() {
        _uiState.update { it.copy(isAddressSelectorOpen = true) }
    }

    fun hideAddressSelector() {
        _uiState.update { it.copy(isAddressSelectorOpen = false) }
    }


    fun placeOrder() {
        val state = _uiState.value
        val userId = currentUserId ?: return
        val userName = currentUserName ?: "Guest"
        val address = state.selectedAddress ?: return

        if (state.cartItems.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isPlacingOrder = true, error = null) }

            val result = orderRepository.placeOrder(
                clientId = userId,
                clientName = userName,
                cartItems = state.cartItems,
                address = address,
                totalAmount = state.totalAmount
            )

            when (result) {
                is Result.Success -> {
                    state.cartItems.forEach { item ->
                        cartRepository.removeFromCart(userId, item.productId)
                    }
                    _uiState.update { it.copy(isPlacingOrder = false, orderSuccess = true) }
                }

                is Result.Failure -> {
                    _uiState.update {
                        it.copy(
                            isPlacingOrder = false,
                            error = result.exception.message
                        )
                    }
                }
            }
        }
    }

    fun onOrderSuccessNavigated() {
        _uiState.update { it.copy(orderSuccess = false) }
    }

}