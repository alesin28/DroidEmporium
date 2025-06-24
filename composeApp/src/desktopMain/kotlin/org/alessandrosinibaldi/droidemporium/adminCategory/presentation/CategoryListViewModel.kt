package org.alessandrosinibaldi.droidemporium.adminCategory.presentation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.core.domain.Result
import kotlinx.coroutines.flow.map
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.AdminProductRepository
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.CategoryRepository


class CategoryListViewModel(
    categoryRepository: CategoryRepository,
    adminProductRepository: AdminProductRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val products: StateFlow<List<Product>> = adminProductRepository
        .searchProducts("")
        .map { result ->
            when (result) {
                is Result.Success -> {
                    result.data
                }
                is Result.Failure -> {
                    println("An error occurred fetching products for CategoryListViewModel: ${result.exception.message}")
                    emptyList()
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val categories: StateFlow<List<Category>> = _searchQuery
        .debounce(300L)
        .flatMapLatest { query ->
            categoryRepository.searchCategories(query)
        }
        .map { result ->
            when (result) {
                is Result.Success -> {
                    result.data
                }
                is Result.Failure -> {
                    println("An error occurred: ${result.exception.message}")
                    emptyList()
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }


}