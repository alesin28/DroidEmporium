package org.alessandrosinibaldi.droidemporium.androidProduct.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.alessandrosinibaldi.droidemporium.androidProduct.domain.ClientProductRepository
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.core.domain.Result


enum class SortOption {
    RELEVANCE, NEWEST, PRICE_ASC, PRICE_DESC
}

data class FilterState(
    val categoryIds: Set<String> = emptySet(),
    val minPrice: Double? = null,
    val maxPrice: Double? = null
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class ProductListViewModel(
    private val categoryId: String?,
    private val categoryName: String?,
    private val showNewest: Boolean,
    private val initialQuery: String?,
    private val clientProductRepository: ClientProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow(initialQuery ?: "")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filterState = MutableStateFlow(
        FilterState(
            categoryIds = if (categoryId != null) setOf(categoryId) else emptySet()
        )
    )
    val filterState = _filterState.asStateFlow()

    private val _sortOption = MutableStateFlow(
        if (showNewest) SortOption.NEWEST else SortOption.RELEVANCE
    )
    val sortOption = _sortOption.asStateFlow()

    private val _screenTitle = MutableStateFlow("")
    val screenTitle = _screenTitle.asStateFlow()

    val isLoading = MutableStateFlow(true)

    val products: StateFlow<List<Product>> = combine(
        _searchQuery.debounce(300L),
        _filterState,
        _sortOption
    ) { query, filters, sort ->
        Triple(query, filters, sort)
    }
        .flatMapLatest { (query, filters, sort) ->
            isLoading.value = true
            val sourceFlow = when {
                query.isNotBlank() -> {
                    clientProductRepository.searchProducts(query)
                }

                filters.categoryIds.isNotEmpty() -> {
                    val categoryIdToQuery = filters.categoryIds.first()
                    clientProductRepository.getProductsByCategory(categoryIdToQuery)
                }

                showNewest -> {
                    clientProductRepository.getNewestProducts(50)
                }

                else -> {
                    flowOf(Result.Success(emptyList()))
                }
            }

            sourceFlow.map { result ->

                val productsList = when (result) {
                    is Result.Success -> result.data
                    is Result.Failure -> emptyList()
                }

                applyFiltersAndSorting(productsList, filters, sort)
            }
        }
        .onEach { finalProductList ->
            isLoading.value = false
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        _screenTitle.value = when {
            categoryName != null -> categoryName
            showNewest -> "New Products"
            !initialQuery.isNullOrBlank() -> "Search Results"
            else -> "Products"
        }
    }

    private fun applyFiltersAndSorting(
        productsList: List<Product>,
        filters: FilterState,
        sort: SortOption
    ): List<Product> {
        val filteredList = productsList.filter { product ->
            val priceMatch = (filters.minPrice == null || product.price >= filters.minPrice) &&
                    (filters.maxPrice == null || product.price <= filters.maxPrice)

            val categoryMatch = if (filters.categoryIds.isEmpty()) {
                true
            } else {
                filters.categoryIds.contains(product.categoryId)
            }
            priceMatch && categoryMatch
        }

        return when (sort) {
            SortOption.RELEVANCE -> filteredList
            SortOption.NEWEST -> filteredList.sortedByDescending { it.createdAt }
            SortOption.PRICE_ASC -> filteredList.sortedBy { it.price }
            SortOption.PRICE_DESC -> filteredList.sortedByDescending { it.price }
        }
    }


    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun updateSelectedCategories(categoryId: String, isSelected: Boolean) {
        _filterState.update { currentState ->
            val newCategories = if (isSelected) {
                currentState.categoryIds + categoryId
            } else {
                currentState.categoryIds - categoryId
            }
            currentState.copy(categoryIds = newCategories)
        }
    }

    fun updatePriceRange(min: Double?, max: Double?) {
        _filterState.update { it.copy(minPrice = min, maxPrice = max) }
    }

    fun updateSortOption(sortOption: SortOption) {
        _sortOption.value = sortOption
    }
}