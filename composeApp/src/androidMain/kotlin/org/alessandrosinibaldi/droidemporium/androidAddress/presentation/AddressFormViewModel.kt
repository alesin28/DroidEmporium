package org.alessandrosinibaldi.droidemporium.androidAddress.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.androidAuth.domain.AuthRepository
import org.alessandrosinibaldi.droidemporium.commonAddress.domain.Address
import org.alessandrosinibaldi.droidemporium.commonAddress.domain.AddressRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result
import java.util.UUID

data class AddressFormUiState(
    val label: String = "Home",
    val name: String = "",
    val surname: String = "",
    val street: String = "",
    val city: String = "",
    val province: String = "",
    val postalCode: String = "",
    val country: String = "Spain",
    val isDefault: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

class AddressFormViewModel(
    private val addressRepository: AddressRepository,
    private val authRepository: AuthRepository,
    private val addressId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressFormUiState())
    val uiState = _uiState.asStateFlow()

    fun onLabelChange(value: String) {
        _uiState.update { it.copy(label = value, error = null) }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, error = null) }
    }

    fun onSurnameChange(value: String) {
        _uiState.update { it.copy(surname = value, error = null) }
    }

    fun onStreetChange(value: String) {
        _uiState.update { it.copy(street = value, error = null) }
    }

    fun onCityChange(value: String) {
        _uiState.update { it.copy(city = value, error = null) }
    }

    fun onProvinceChange(value: String) {
        _uiState.update { it.copy(province = value, error = null) }
    }

    fun onPostalCodeChange(value: String) {
        _uiState.update { it.copy(postalCode = value, error = null) }
    }

    fun onCountryChange(value: String) {
        _uiState.update { it.copy(country = value, error = null) }
    }

    fun onIsDefaultChange(value: Boolean) {
        _uiState.update { it.copy(isDefault = value, error = null) }
    }

    fun saveAddress() {
        val state = _uiState.value

        if (state.name.isBlank() || state.street.isBlank() || state.city.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all required fields.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val user = authRepository.getCurrentUser().firstOrNull()
            if (user == null) {
                _uiState.update { it.copy(isLoading = false, error = "User not authenticated.") }
                return@launch
            }

            val address = Address(
                id = addressId ?: UUID.randomUUID().toString(),
                label = state.label.trim(),
                name = state.name.trim(),
                surname = state.surname.trim(),
                street = state.street.trim(),
                city = state.city.trim(),
                province = state.province.trim(),
                postalCode = state.postalCode.trim(),
                country = state.country.trim(),
                isDefault = state.isDefault
            )

            val result = if (addressId == null) {
                addressRepository.addAddress(user.id, address)
            } else {
                addressRepository.updateAddress(user.id, address)
            }

            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        saveSuccess = true
                    )
                }

                is Result.Failure -> _uiState.update {
                    it.copy(isLoading = false, error = result.exception.message)
                }
            }
        }
    }

    fun onSaveSuccessNavigated() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}