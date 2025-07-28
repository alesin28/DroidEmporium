package org.alessandrosinibaldi.droidemporium.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.alessandrosinibaldi.droidemporium.androidProduct.domain.ClientProductRepository
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.core.domain.Result

data class HomeScreenState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val newProducts: List<Product> = emptyList(),
    val error: String? = null
)

class HomeViewModel(
    clientProductRepository: ClientProductRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    val uiState: StateFlow<HomeScreenState> = combine(
        categoryRepository.searchCategories(""),
        clientProductRepository.getNewestProducts(limit = 10)
    ) { categoriesResult, newProductsResult ->

        val categories = when (categoriesResult) {
            is Result.Success -> categoriesResult.data
            is Result.Failure -> emptyList()
        }

        val newProducts = when (newProductsResult) {
            is Result.Success -> newProductsResult.data
            is Result.Failure -> emptyList()
        }

        val errorMessage = if (categoriesResult is Result.Failure || newProductsResult is Result.Failure) {
            "Failed to load home screen data."
        } else null

        HomeScreenState(
            isLoading = false,
            categories = categories,
            newProducts = newProducts,
            error = errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeScreenState(isLoading = true)
    )
}