package org.alessandrosinibaldi.droidemporium.adminProduct.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.ProductRepository

class ProductListViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    enum class SortColumn {
        NAME, PRICE, NONE
    }

    enum class SortDirection {
        ASCENDING, DESCENDING
    }

    private val _sortColumn = MutableStateFlow(SortColumn.NAME)
    val sortColumn: StateFlow<SortColumn> = _sortColumn.asStateFlow()

    private val _sortDirection = MutableStateFlow(SortDirection.ASCENDING)
    val sortDirection: StateFlow<SortDirection> = _sortDirection.asStateFlow()

    private val _minPriceFilter = MutableStateFlow<Double>(0.0)
    val minPriceFilter: StateFlow<Double> = _minPriceFilter.asStateFlow()

    private val _maxPriceFilter = MutableStateFlow<Double>(999999.0)
    val maxPriceFilter: StateFlow<Double> = _maxPriceFilter.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = combine(
        repository.searchProducts(),
        _sortColumn,
        _sortDirection,
        _minPriceFilter,
        _maxPriceFilter
    ) { productsList, column, direction, minPriceFilter, maxPriceFilter ->
        val sortedList = when (column) {
            SortColumn.NAME -> {
                if (direction == SortDirection.ASCENDING) {
                    productsList.sortedBy { it.name }
                } else {
                    productsList.sortedByDescending { it.name }
                }
            }

            SortColumn.PRICE -> {
                if (direction == SortDirection.ASCENDING) {
                    productsList.sortedBy { it.price }
                } else {
                    productsList.sortedByDescending { it.price }
                }
            }

            SortColumn.NONE -> productsList
        }

        sortedList.filter { product -> product.price >= minPriceFilter }
        sortedList.filter { product -> product.price <= maxPriceFilter }
    }.stateIn(
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

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

}