package org.alessandrosinibaldi.droidemporium.androidAuth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.androidAuth.domain.AuthRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

sealed interface AuthState {
    object Loading : AuthState
    object Authenticated : AuthState
    object Unauthenticated : AuthState
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false
)

data class SignupUiState(
    val displayName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val signupSuccess: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {


    val authState: StateFlow<AuthState> = authRepository.getCurrentUser()
        .map { firebaseUser ->
            if (firebaseUser != null) {
                AuthState.Authenticated
            } else {
                AuthState.Unauthenticated
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthState.Loading
        )


    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }


    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = authRepository.login(
                email = _uiState.value.email.trim(),
                password = _uiState.value.password
            )

            when (result) {
                is Result.Success -> {

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginSuccess = true
                        )
                    }
                }

                is Result.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.exception.message ?: "An unknown login error occurred."
                        )
                    }
                }
            }
        }
    }


    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }


    fun onLoginSuccessNavigated() {
        _uiState.update { it.copy(loginSuccess = false) }
    }

    private val _signupUiState = MutableStateFlow(SignupUiState())
    val signupUiState = _signupUiState.asStateFlow()

    fun onSignupDisplayNameChange(name: String) {
        _signupUiState.update {
            it.copy(
                displayName = name,
                error = null
            )
        }
    }

    fun onSignupEmailChange(email: String) {
        _signupUiState.update {
            it.copy(
                email = email,
                error = null
            )
        }
    }

    fun onSignupPhoneNumberChange(phone: String) {
        _signupUiState.update {
            it.copy(
                phoneNumber = phone,
                error = null
            )
        }
    }

    fun onSignupPasswordChange(password: String) {
        _signupUiState.update {
            it.copy(
                password = password,
                error = null
            )
        }
    }

    fun onSignupConfirmPasswordChange(password: String) {
        _signupUiState.update {
            it.copy(
                confirmPassword = password,
                error = null
            )
        }
    }

    fun signup() {
        val state = _signupUiState.value

        if (state.password != state.confirmPassword) {
            _signupUiState.update { it.copy(error = "Passwords are not the same") }
            return
        }

        viewModelScope.launch {
            _signupUiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            val result = authRepository.signup(
                displayName = state.displayName.trim(),
                email = state.email.trim(),
                phoneNumber = state.phoneNumber.trim().takeIf { it.isNotEmpty() },
                password = state.password
            )

            when (result) {
                is Result.Success -> {
                    _signupUiState.update {
                        it.copy(
                            isLoading = false,
                            signupSuccess = true
                        )
                    }
                }

                is Result.Failure -> {
                    _signupUiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.exception.message ?: "An unknown signup error occurred."
                        )
                    }
                }
            }
        }
    }

    fun onSignupSuccessNavigated() {
        _signupUiState.update { it.copy(signupSuccess = false) }
    }


}