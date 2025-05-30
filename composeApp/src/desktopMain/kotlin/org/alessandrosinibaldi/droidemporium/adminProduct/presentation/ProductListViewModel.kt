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
import org.alessandrosinibaldi.droidemporium.adminCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.adminCategory.domain.CategoryRepository
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.ProductRepository

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,

    ) : ViewModel() {

    enum class SortColumn {
        NAME, PRICE, STOCK, NONE
    }

    enum class SortDirection {
        ASCENDING, DESCENDING
    }

    private val _sortColumn = MutableStateFlow(SortColumn.NAME)
    //val sortColumn: StateFlow<SortColumn> = _sortColumn.asStateFlow()

    private val _sortDirection = MutableStateFlow(SortDirection.ASCENDING)
    //val sortDirection: StateFlow<SortDirection> = _sortDirection.asStateFlow()

    private val _minPriceFilter = MutableStateFlow<Double>(0.0)
    val minPriceFilter: StateFlow<Double> = _minPriceFilter.asStateFlow()

    private val _maxPriceFilter = MutableStateFlow<Double>(999999.0)
    val maxPriceFilter: StateFlow<Double> = _maxPriceFilter.asStateFlow()

    private val _minStockFilter = MutableStateFlow<Int>(0)
    val minStockFilter: StateFlow<Int> = _minStockFilter.asStateFlow()

    private val _maxStockFilter = MutableStateFlow<Int>(999999)
    val maxStockFilter: StateFlow<Int> = _maxStockFilter.asStateFlow()

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


    //private val _products = MutableStateFlow<List<Product>>(emptyList())
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val products: StateFlow<List<Product>> = combine(
        _searchQuery.debounce(300L)
            .flatMapLatest { productQuery ->
                productRepository.searchProducts(productQuery)
            },
        sortStateFlow,
        filterStateFlow,
        activeFilterStateFlow
    ) { productsList, sortState, filterState, activeFilterState ->
        val sortedList = when (sortState.sortColumn) {
            SortColumn.NAME -> {
                if (sortState.sortDirection == SortDirection.ASCENDING) {
                    productsList.sortedBy { it.name }
                } else {
                    productsList.sortedByDescending { it.name }
                }
            }

            SortColumn.PRICE -> {
                if (sortState.sortDirection == SortDirection.ASCENDING) {
                    productsList.sortedBy { it.price }
                } else {
                    productsList.sortedByDescending { it.price }
                }
            }

            SortColumn.STOCK -> {
                if (sortState.sortDirection == SortDirection.ASCENDING) {
                    productsList.sortedBy { it.stock }
                } else {
                    productsList.sortedByDescending { it.stock }
                }
            }

            SortColumn.NONE -> productsList
        }

        sortedList.filter { product ->
            val priceStockMatch =
                product.price >= filterState.minPrice && product.price <= filterState.maxPrice &&
                        product.stock >= filterState.minStock && product.stock <= filterState.maxStock

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
            priceStockMatch && statusMatch && categoryMatch
        }
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val categories: StateFlow<List<Category>> = categoryRepository.searchCategories()
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

    fun updateMinPriceFilter(minPrice: Double) {
        if (minPrice.isNaN()) return
        _minPriceFilter.value = minPrice
    }

    fun updateMaxPriceFilter(maxPrice: Double) {
        if (maxPrice.isNaN()) return
        _maxPriceFilter.value = maxPrice
    }

    fun updateMinStockFilter(minStock: Int) {
        if (minStock < 0) return
        _minStockFilter.value = minStock
    }

    fun updateMaxStockFilter(maxStock: Int) {
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

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
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
        val minPrice: Double,
        val maxPrice: Double,
        val minStock: Int,
        val maxStock: Int,
        val selectedCategories: Set<String>
    )

    data class ActiveFilterState(
        val isActive: Boolean,
        val isInactive: Boolean
    )

}

