package org.alessandrosinibaldi.droidemporium.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.alessandrosinibaldi.droidemporium.androidProduct.domain.ClientProductRepository
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.core.domain.Result


class HomeViewModel(
    private val clientProductRepository: ClientProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _newArrivals = MutableStateFlow<List<Product>>(emptyList())
    val newArrivals = _newArrivals.asStateFlow()

    init {
        loadCategories()
        loadNewArrivals()
    }

    private fun loadCategories() {
        categoryRepository.searchCategories("")
            .onEach { result ->
                when (result) {
                    is Result.Success -> {
                        _categories.value = result.data
                    }
                    is Result.Failure -> {
                        val errorMessage = "Failed to load categories"
                        println("$errorMessage: ${result.exception.message}")
                    }
                }
                if (_isLoading.value) {
                    _isLoading.value = false
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadNewArrivals() {
        clientProductRepository.getNewestProducts(limit = 10)
            .onEach { result ->
                when (result) {
                    is Result.Success -> {
                        _newArrivals.value = result.data
                    }
                    is Result.Failure -> {
                        val errorMessage = "Failed to load new arrivals"
                        println("$errorMessage: ${result.exception.message}")
                    }
                }
            }
            .launchIn(viewModelScope)
    }

}