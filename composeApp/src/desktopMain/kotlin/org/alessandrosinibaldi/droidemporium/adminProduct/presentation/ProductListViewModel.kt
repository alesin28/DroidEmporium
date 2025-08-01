package org.alessandrosinibaldi.droidemporium.adminProduct.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.core.domain.Result
import kotlinx.coroutines.flow.map
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.AdminProductRepository
import org.alessandrosinibaldi.droidemporium.adminReview.domain.AdminReviewRepository
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.commonReview.domain.Review
import kotlin.math.roundToInt

class ProductListViewModel(
    private val adminProductRepository: AdminProductRepository,
    private val categoryRepository: CategoryRepository,
    private val adminReviewRepository: AdminReviewRepository
) : ViewModel() {

    data class ProductWithAverageRating(
        val product: Product,
        val averageRating: Double
    )

    enum class SortColumn {
        NAME, PRICE, STOCK, AVERAGE_RATING, NONE
    }

    enum class SortDirection {
        ASCENDING, DESCENDING
    }

    private val _sortColumn = MutableStateFlow(SortColumn.NAME)
    val sortColumn: StateFlow<SortColumn> = _sortColumn.asStateFlow()

    private val _sortDirection = MutableStateFlow(SortDirection.ASCENDING)
    val sortDirection: StateFlow<SortDirection> = _sortDirection.asStateFlow()

    private val _minPriceFilter = MutableStateFlow<Double?>(null)
    val minPriceFilter: StateFlow<Double?> = _minPriceFilter.asStateFlow()

    private val _maxPriceFilter = MutableStateFlow<Double?>(null)
    val maxPriceFilter: StateFlow<Double?> = _maxPriceFilter.asStateFlow()

    private val _minStockFilter = MutableStateFlow<Int?>(null)
    val minStockFilter: StateFlow<Int?> = _minStockFilter.asStateFlow()

    private val _maxStockFilter = MutableStateFlow<Int?>(null)
    val maxStockFilter: StateFlow<Int?> = _maxStockFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isActiveFilter = MutableStateFlow<Boolean>(true)
    val isActiveFilter: StateFlow<Boolean> = _isActiveFilter.asStateFlow()
    private val _isInactiveFilter = MutableStateFlow<Boolean>(true)
    val isInactiveFilter: StateFlow<Boolean> = _isInactiveFilter.asStateFlow()

    private val _selectedCategoryIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedCategoryIds = _selectedCategoryIds.asStateFlow()


    private val sortStateFlow: Flow<SortState> = combine(
        _sortColumn,
        _sortDirection,
    ) { sortColumn, sortDirection ->
        SortState(
            sortColumn = sortColumn,
            sortDirection = sortDirection,
        )
    }

    private val filterStateFlow: Flow<FilterState> = combine(
        _minPriceFilter,
        _maxPriceFilter,
        _minStockFilter,
        _maxStockFilter,
        _selectedCategoryIds
    ) { minPrice, maxPrice, minStock, maxStock, selectedCategories ->
        FilterState(
            minPrice = minPrice,
            maxPrice = maxPrice,
            minStock = minStock,
            maxStock = maxStock,
            selectedCategories = selectedCategories
        )
    }

    private val activeFilterStateFlow: Flow<ActiveFilterState> = combine(
        _isActiveFilter,
        _isInactiveFilter,
    ) { isActiveFilter, isInactiveFilter ->
        ActiveFilterState(
            isActive = isActiveFilter,
            isInactive = isInactiveFilter
        )
    }

    private val reviewsFlow: StateFlow<List<Review>> = adminReviewRepository.getAllReviews()
        .map { result ->
            when (result) {
                is Result.Success -> result.data
                is Result.Failure -> {
                    println("Error fetching reviews: ${result.exception.message}")
                    emptyList()
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val productsWithRatings: StateFlow<List<ProductWithAverageRating>> = run {
        val productsSourceFlow: Flow<List<Product>> = _searchQuery
            .debounce(300L)
            .flatMapLatest { productQuery ->
                adminProductRepository.searchProducts(productQuery)
            }
            .map { result ->
                when (result) {
                    is Result.Success -> result.data
                    is Result.Failure -> {
                        println("Error searching products: ${result.exception.message}")
                        emptyList()
                    }
                }
            }

        combine(
            productsSourceFlow,
            reviewsFlow,
            sortStateFlow,
            filterStateFlow,
            activeFilterStateFlow
        ) { productsList, reviewsList, sortState, filterState, activeFilterState ->

            val reviewsByProductId = reviewsList.groupBy { it.productId }

            val filteredAndRatedList = productsList.filter { product ->
                val priceMatch =
                    (filterState.minPrice == null || product.price >= filterState.minPrice) &&
                            (filterState.maxPrice == null || product.price <= filterState.maxPrice)

                val stockMatch =
                    (filterState.minStock == null || product.stock >= filterState.minStock) &&
                            (filterState.maxStock == null || product.stock <= filterState.maxStock)

                val statusMatch = if (activeFilterState.isActive == activeFilterState.isInactive) {
                    true
                } else {
                    product.isActive == activeFilterState.isActive
                }

                val categoryMatch = if (filterState.selectedCategories.isEmpty()) {
                    true
                } else {
                    filterState.selectedCategories.contains(product.categoryId)
                }
                priceMatch && stockMatch && statusMatch && categoryMatch
            }.map { product ->
                val productReviews = reviewsByProductId[product.id] ?: emptyList()
                val averageRating = if (productReviews.isNotEmpty()) {
                    productReviews.sumOf { it.rating }.toDouble() / productReviews.size
                } else {
                    0.0
                }
                val roundedRating = (averageRating * 10).roundToInt() / 10.0

                ProductWithAverageRating(
                    product = product,
                    averageRating = roundedRating
                )
            }

            when (sortState.sortColumn) {
                SortColumn.NAME -> {
                    if (sortState.sortDirection == SortDirection.ASCENDING) {
                        filteredAndRatedList.sortedBy { it.product.name }
                    } else {
                        filteredAndRatedList.sortedByDescending { it.product.name }
                    }
                }
                SortColumn.PRICE -> {
                    if (sortState.sortDirection == SortDirection.ASCENDING) {
                        filteredAndRatedList.sortedBy { it.product.price }
                    } else {
                        filteredAndRatedList.sortedByDescending { it.product.price }
                    }
                }
                SortColumn.STOCK -> {
                    if (sortState.sortDirection == SortDirection.ASCENDING) {
                        filteredAndRatedList.sortedBy { it.product.stock }
                    } else {
                        filteredAndRatedList.sortedByDescending { it.product.stock }
                    }
                }
                SortColumn.AVERAGE_RATING -> {
                    if (sortState.sortDirection == SortDirection.ASCENDING) {
                        filteredAndRatedList.sortedBy { it.averageRating }
                    } else {
                        filteredAndRatedList.sortedByDescending { it.averageRating }
                    }
                }
                SortColumn.NONE -> filteredAndRatedList
            }
        }.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    val categories: StateFlow<List<Category>> = categoryRepository
        .searchCategories("")
        .map { result ->
            when (result) {
                is Result.Success -> result.data
                is Result.Failure -> {
                    println("Error fetching categories for filter list: ${result.exception.message}")
                    emptyList()
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateSort(clickedColumn: SortColumn) {
        if (clickedColumn == SortColumn.NONE) return

        if (_sortColumn.value == clickedColumn) {
            _sortDirection.value = when (_sortDirection.value) {
                SortDirection.ASCENDING -> SortDirection.DESCENDING
                SortDirection.DESCENDING -> SortDirection.ASCENDING
            }
        } else {
            _sortColumn.value = clickedColumn
            _sortDirection.value = SortDirection.ASCENDING
        }
    }

    fun updateMinPriceFilter(minPrice: Double?) {
        _minPriceFilter.value = minPrice
    }

    fun updateMaxPriceFilter(maxPrice: Double?) {
        _maxPriceFilter.value = maxPrice
    }

    fun updateMinStockFilter(minStock: Int?) {
        _minStockFilter.value = minStock
    }

    fun updateMaxStockFilter(maxStock: Int?) {
        _maxStockFilter.value = maxStock
    }

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateActiveFilter(isActive: Boolean) {
        _isActiveFilter.value = isActive
    }

    fun updateInactiveFilter(isInactive: Boolean) {
        _isInactiveFilter.value = isInactive
    }

    fun changeProductStatus(product: Product) {
        viewModelScope.launch {
            val result = adminProductRepository.changeProductState(product.id)
            when (result) {
                is Result.Success -> {
                    println("Successfully changed status of product ${product.id}")
                }

                is Result.Failure -> {
                    println("Failed to change status of product ${product.id}: ${result.exception.message}")
                }
            }
        }
    }

    fun updateSelectedCategories(categoryId: String, isSelected: Boolean) {
        val currentSelection = _selectedCategoryIds.value
        val newSelection = if (isSelected) {
            currentSelection + categoryId
        } else {
            currentSelection - categoryId
        }
        _selectedCategoryIds.value = newSelection
    }


    data class SortState(
        val sortColumn: SortColumn,
        val sortDirection: SortDirection
    )

    data class FilterState(
        val minPrice: Double?,
        val maxPrice: Double?,
        val minStock: Int?,
        val maxStock: Int?,
        val selectedCategories: Set<String>
    )

    data class ActiveFilterState(
        val isActive: Boolean,
        val isInactive: Boolean
    )

}