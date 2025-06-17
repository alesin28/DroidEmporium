package org.alessandrosinibaldi.droidemporium.adminClient.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client
import org.alessandrosinibaldi.droidemporium.core.domain.Result
import kotlinx.coroutines.flow.map
import org.alessandrosinibaldi.droidemporium.adminClient.domain.AdminClientRepository


class ClientListViewModel(
    private val adminClientRepository: AdminClientRepository,

    ) : ViewModel() {

    enum class SortColumn {
        NAME, NONE
    }

    enum class SortDirection {
        ASCENDING, DESCENDING
    }

    private val _sortColumn = MutableStateFlow(SortColumn.NAME)
    //val sortColumn: StateFlow<SortColumn> = _sortColumn.asStateFlow()

    private val _sortDirection = MutableStateFlow(SortDirection.ASCENDING)
    //val sortDirection: StateFlow<SortDirection> = _sortDirection.asStateFlow()


    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isActiveFilter = MutableStateFlow<Boolean>(true)
    val isActiveFilter: StateFlow<Boolean> = _isActiveFilter.asStateFlow()
    private val _isInactiveFilter = MutableStateFlow<Boolean>(true)
    val isInactiveFilter: StateFlow<Boolean> = _isInactiveFilter.asStateFlow()

    private val sortStateFlow: Flow<SortState> = combine(
        _sortColumn,
        _sortDirection,
    ) { sortColumn, sortDirection ->
        SortState(
            sortColumn = sortColumn,
            sortDirection = sortDirection,
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


    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val clients: StateFlow<List<Client>> = run {
        val clientsSourceFlow: Flow<Result<List<Client>>> = _searchQuery
            .debounce(300L)
            .flatMapLatest { clientQuery ->
                adminClientRepository.searchClients(clientQuery)
            }

        val unwrappedClientsFlow: Flow<List<Client>> = clientsSourceFlow
            .map { result ->
                when (result) {
                    is Result.Success -> result.data
                    is Result.Failure -> {
                        println("Error searching clients: ${result.exception.message}")
                        emptyList()
                    }
                }
            }

        combine(
            unwrappedClientsFlow, // <-- Use the clean flow here
            sortStateFlow,
            activeFilterStateFlow
        ) { clientList, sortState, activeFilterState ->
            val sortedList = when (sortState.sortColumn) {
                SortColumn.NAME -> {
                    if (sortState.sortDirection == SortDirection.ASCENDING) {
                        clientList.sortedBy { it.displayName }
                    } else {
                        clientList.sortedByDescending { it.displayName }
                    }
                }

                SortColumn.NONE -> clientList
            }

            sortedList.filter { client ->
                val statusMatch = if (activeFilterState.isActive == activeFilterState.isInactive) {
                    true
                } else {
                    client.isActive == activeFilterState.isActive
                }
                statusMatch
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
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

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateActiveFilter(isActive: Boolean) {
        _isActiveFilter.value = isActive
    }

    fun updateInactiveFilter(isInactive: Boolean) {
        _isInactiveFilter.value = isInactive
    }


    data class SortState(
        val sortColumn: SortColumn,
        val sortDirection: SortDirection
    )

    data class ActiveFilterState(
        val isActive: Boolean,
        val isInactive: Boolean
    )
}