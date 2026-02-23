package org.alessandrosinibaldi.droidemporium.androidClient.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.androidAuth.domain.AuthRepository
import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client

data class ProfileUiState(
    val client: Client? = null,
    val isLoading: Boolean = true
)

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = authRepository.getCurrentUser()
        .map { client -> ProfileUiState(client = client, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileUiState(isLoading = true)
        )

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}