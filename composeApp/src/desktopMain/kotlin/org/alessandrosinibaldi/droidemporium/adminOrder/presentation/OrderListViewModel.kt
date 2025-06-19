package org.alessandrosinibaldi.droidemporium.adminOrder.presentation

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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.AdminOrderRepository
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.core.domain.Result

class OrderListViewModel(
    private val adminOrderRepository: AdminOrderRepository
) : ViewModel() {

    enum class SortColumn {
        ORDER_DATE, TOTAL_AMOUNT, TOTAL_QUANTITY, NONE
    }

    enum class SortDirection {
        ASCENDING, DESCENDING
    }

    private val _sortColumn = MutableStateFlow(SortColumn.ORDER_DATE)
    val sortColumn: StateFlow<SortColumn> = _sortColumn.asStateFlow()

    private val _sortDirection = MutableStateFlow(SortDirection.DESCENDING) // Default to newest first
    val sortDirection: StateFlow<SortDirection> = _sortDirection.asStateFlow()

    private val _minTotalAmountFilter = MutableStateFlow<Double?>(null)
    val minTotalAmountFilter: StateFlow<Double?> = _minTotalAmountFilter.asStateFlow()

    private val _maxTotalAmountFilter = MutableStateFlow<Double?>(null)
    val maxTotalAmountFilter: StateFlow<Double?> = _maxTotalAmountFilter.asStateFlow()

    private val _minTotalQuantityFilter = MutableStateFlow<Int?>(null)
    val minTotalQuantityFilter: StateFlow<Int?> = _minTotalQuantityFilter.asStateFlow()

    private val _maxTotalQuantityFilter = MutableStateFlow<Int?>(null)
    val maxTotalQuantityFilter: StateFlow<Int?> = _maxTotalQuantityFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val sortStateFlow: Flow<SortState> = combine(
        _sortColumn,
        _sortDirection,
    ) { sortColumn, sortDirection ->
        SortState(sortColumn, sortDirection)
    }

    private val filterStateFlow: Flow<FilterState> = combine(
        _minTotalAmountFilter,
        _maxTotalAmountFilter,
        _minTotalQuantityFilter,
        _maxTotalQuantityFilter
    ) { minAmount, maxAmount, minQty, maxQty ->
        FilterState(minAmount, maxAmount, minQty, maxQty)
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val orders: StateFlow<List<Order>> = run {
        val ordersSourceFlow: Flow<List<Order>> = _searchQuery
            .debounce(300L)
            .flatMapLatest { query ->
                adminOrderRepository.searchOrders(query)
            }
            .map { result ->
                when (result) {
                    is Result.Success -> result.data
                    is Result.Failure -> {
                        println("Error searching orders: ${result.exception.message}")
                        emptyList()
                    }
                }
            }

        combine(
            ordersSourceFlow,
            sortStateFlow,
            filterStateFlow
        ) { ordersList, sortState, filterState ->

            val sortedList = when (sortState.sortColumn) {
                SortColumn.ORDER_DATE -> {
                    if (sortState.sortDirection == SortDirection.ASCENDING) {
                        ordersList.sortedBy { it.orderDate }
                    } else {
                        ordersList.sortedByDescending { it.orderDate }
                    }
                }
                SortColumn.TOTAL_AMOUNT -> {
                    if (sortState.sortDirection == SortDirection.ASCENDING) {
                        ordersList.sortedBy { it.totalAmount }
                    } else {
                        ordersList.sortedByDescending { it.totalAmount }
                    }
                }
                SortColumn.TOTAL_QUANTITY -> {
                    if (sortState.sortDirection == SortDirection.ASCENDING) {
                        ordersList.sortedBy { it.lines.sumOf { line -> line.quantity } }
                    } else {
                        ordersList.sortedByDescending { it.lines.sumOf { line -> line.quantity } }
                    }
                }
                SortColumn.NONE -> ordersList
            }

            sortedList.filter { order ->
                val totalQuantity = order.lines.sumOf { it.quantity }

                val amountMatch =
                    (filterState.minTotalAmount == null || order.totalAmount >= filterState.minTotalAmount) &&
                            (filterState.maxTotalAmount == null || order.totalAmount <= filterState.maxTotalAmount)

                val quantityMatch =
                    (filterState.minTotalQuantity == null || totalQuantity >= filterState.minTotalQuantity) &&
                            (filterState.maxTotalQuantity == null || totalQuantity <= filterState.maxTotalQuantity)

                amountMatch && quantityMatch
            }
        }.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

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

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateMinTotalAmountFilter(minAmount: Double?) {
        _minTotalAmountFilter.value = minAmount
    }

    fun updateMaxTotalAmountFilter(maxAmount: Double?) {
        _maxTotalAmountFilter.value = maxAmount
    }

    fun updateMinTotalQuantityFilter(minQty: Int?) {
        _minTotalQuantityFilter.value = minQty
    }

    fun updateMaxTotalQuantityFilter(maxQty: Int?) {
        _maxTotalQuantityFilter.value = maxQty
    }


    // --- Internal State-Holding Data Classes ---

    data class SortState(
        val sortColumn: SortColumn,
        val sortDirection: SortDirection
    )

    data class FilterState(
        val minTotalAmount: Double?,
        val maxTotalAmount: Double?,
        val minTotalQuantity: Int?,
        val maxTotalQuantity: Int?
    )
}