package org.alessandrosinibaldi.droidemporium.androidReview.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.androidAuth.domain.AuthRepository
import org.alessandrosinibaldi.droidemporium.androidReview.domain.ClientReviewRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result

data class ReviewFormUiState(
    val rating: Int = 0,
    val content: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class ReviewFormViewModel(
    val productId: String,
    val productName: String,
    private val reviewRepository: ClientReviewRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewFormUiState())
    val uiState = _uiState.asStateFlow()

    fun onRatingChange(rating: Int) {
        _uiState.update { it.copy(rating = rating, error = null) }
    }

    fun onContentChange(content: String) {
        _uiState.update { it.copy(content = content, error = null) }
    }

    fun submitReview() {
        val state = _uiState.value
        if (state.rating == 0) {
            _uiState.update { it.copy(error = "Please select a rating.") }
            return
        }
        if (state.content.isBlank()) {
            _uiState.update { it.copy(error = "Please write a review.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val user = authRepository.getCurrentUser().firstOrNull()

            if (user == null) {
                _uiState.update { it.copy(isLoading = false, error = "User not authenticated.") }
                return@launch
            }

            val result = reviewRepository.addReview(
                productId = productId,
                clientId = user.id,
                clientDisplayName = user.displayName,
                rating = state.rating,
                content = state.content.trim()
            )

            when (result) {
                is Result.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }

                is Result.Failure -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exception.message
                    )
                }
            }
        }
    }

    fun onSuccessNavigated() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}