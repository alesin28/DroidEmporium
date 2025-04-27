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

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = combine(
        repository.searchProducts(),
        _sortColumn,
        _sortDirection
    ) { productsList, column, direction ->
        when (column) {
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

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

}