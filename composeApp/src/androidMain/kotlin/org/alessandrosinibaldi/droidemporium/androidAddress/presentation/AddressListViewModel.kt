package org.alessandrosinibaldi.droidemporium.androidAddress.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.androidAuth.domain.AuthRepository
import org.alessandrosinibaldi.droidemporium.commonAddress.domain.Address
import org.alessandrosinibaldi.droidemporium.commonAddress.domain.AddressRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

data class AddressListUiState(
    val addresses: List<Address> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class AddressListViewModel(
    private val authRepository: AuthRepository,
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressListUiState())
    val uiState = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().collectLatest { user ->
                currentUserId = user?.id
                if (user != null) {
                    loadAddresses()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "User not authenticated") }
                }
            }
        }
    }

    fun loadAddresses() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = addressRepository.getAddressesForClient(userId)) {
                is Result.Success -> {
                    val sortedList = result.data.sortedByDescending { it.isDefault }
                    _uiState.update { it.copy(addresses = sortedList, isLoading = false) }
                }

                is Result.Failure -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.exception.message)
                    }
                }
            }
        }
    }

    fun deleteAddress(addressId: String) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (addressRepository.deleteAddress(userId, addressId)) {
                is Result.Success -> loadAddresses()
                is Result.Failure -> _uiState.update { it.copy(isLoading = false) }
            }
        }


    }

    fun setDefaultAddress(addressId: String) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (addressRepository.setDefaultAddress(userId, addressId)) {
                is Result.Success -> loadAddresses()
                is Result.Failure -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}