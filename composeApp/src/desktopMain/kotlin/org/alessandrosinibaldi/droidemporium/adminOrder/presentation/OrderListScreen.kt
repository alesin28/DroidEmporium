package org.alessandrosinibaldi.droidemporium.adminOrder.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.alessandrosinibaldi.droidemporium.adminOrder.presentation.OrderListViewModel.SortColumn
import org.alessandrosinibaldi.droidemporium.adminOrder.presentation.OrderListViewModel.SortDirection
import org.alessandrosinibaldi.droidemporium.app.Route
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.core.components.MenuReturnButton
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import androidx.compose.foundation.interaction.PressInteraction
import kotlin.time.Instant
import kotlin.time.toJavaInstant


@Composable
fun orderListScreen(
    viewModel: OrderListViewModel = koinViewModel(),
    navController: NavHostController
) {
    val orders by viewModel.orders.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val sortColumn by viewModel.sortColumn.collectAsState()
    val sortDirection by viewModel.sortDirection.collectAsState()
    val minTotalAmount by viewModel.minTotalAmountFilter.collectAsState()
    val maxTotalAmount by viewModel.maxTotalAmountFilter.collectAsState()
    val minTotalQuantity by viewModel.minTotalQuantityFilter.collectAsState()
    val maxTotalQuantity by viewModel.maxTotalQuantityFilter.collectAsState()
    val startDate by viewModel.startDateFilter.collectAsState()
    val endDate by viewModel.endDateFilter.collectAsState()

    val onNavigateToOrderDetail: (String) -> Unit = { orderId ->
        navController.navigate(Route.OrderDetail(orderId = orderId))
    }

    val onNavigateBack: () -> Unit = {
        navController.popBackStack()
    }

    orderListScreenContent(
        orders = orders,
        query = query,
        sortColumn = sortColumn,
        sortDirection = sortDirection,
        minTotalAmount = minTotalAmount,
        maxTotalAmount = maxTotalAmount,
        minTotalQuantity = minTotalQuantity,
        maxTotalQuantity = maxTotalQuantity,
        startDate = startDate,
        endDate = endDate,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onSortClick = viewModel::updateSort,
        onNavigateToOrderDetail = onNavigateToOrderDetail,
        onMinTotalAmountChange = viewModel::updateMinTotalAmountFilter,
        onMaxTotalAmountChange = viewModel::updateMaxTotalAmountFilter,
        onMinTotalQuantityChange = viewModel::updateMinTotalQuantityFilter,
        onMaxTotalQuantityChange = viewModel::updateMaxTotalQuantityFilter,
        onStartDateChange = viewModel::updateStartDateFilter,
        onEndDateChange = viewModel::updateEndDateFilter,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun orderListScreenContent(
    orders: List<Order>,
    query: String,
    sortColumn: SortColumn,
    sortDirection: SortDirection,
    minTotalAmount: Double?,
    maxTotalAmount: Double?,
    minTotalQuantity: Int?,
    maxTotalQuantity: Int?,
    startDate: Instant?,
    endDate: Instant?,
    onSearchQueryChange: (String) -> Unit,
    onSortClick: (SortColumn) -> Unit,
    onNavigateToOrderDetail: (String) -> Unit,
    onMinTotalAmountChange: (Double?) -> Unit,
    onMaxTotalAmountChange: (Double?) -> Unit,
    onMinTotalQuantityChange: (Int?) -> Unit,
    onMaxTotalQuantityChange: (Int?) -> Unit,
    onStartDateChange: (Instant?) -> Unit,
    onEndDateChange: (Instant?) -> Unit,
    onNavigateBack: () -> Unit
) {
    val orderIdWeight = 1.5f
    val clientNameWeight = 2f
    val dateWeight = 1.5f
    val quantityWeight = 1f
    val amountWeight = 1f

    var minAmountInput by remember { mutableStateOf(minTotalAmount?.toString() ?: "") }
    var maxAmountInput by remember { mutableStateOf(maxTotalAmount?.toString() ?: "") }
    var minQuantityInput by remember { mutableStateOf(minTotalQuantity?.toString() ?: "") }
    var maxQuantityInput by remember { mutableStateOf(maxTotalQuantity?.toString() ?: "") }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    fun formatInstant(instant: Instant?): String {
        return instant?.let { dateFormatter.format(Date.from(it.toJavaInstant())) } ?: ""
    }

    LaunchedEffect(orders) {
        listState.scrollToItem(index = 0)
    }

    LaunchedEffect(minTotalAmount) { minAmountInput = minTotalAmount?.toString() ?: "" }
    LaunchedEffect(maxTotalAmount) { maxAmountInput = maxTotalAmount?.toString() ?: "" }
    LaunchedEffect(minTotalQuantity) { minQuantityInput = minTotalQuantity?.toString() ?: "" }
    LaunchedEffect(maxTotalQuantity) { maxQuantityInput = maxTotalQuantity?.toString() ?: "" }


    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = startDate?.toEpochMilliseconds()
    )
    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = endDate?.toEpochMilliseconds()
    )


    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = startDatePickerState.selectedDateMillis?.let {
                            Instant.fromEpochMilliseconds(it)
                        }
                        onStartDateChange(selectedDate)
                        showStartDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = endDatePickerState.selectedDateMillis?.let {
                            val endOfDayMillis = it + TimeUnit.DAYS.toMillis(1) - 1
                            Instant.fromEpochMilliseconds(endOfDayMillis)
                        }
                        onEndDateChange(selectedDate)
                        showEndDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }


    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MenuReturnButton(onNavigateBack = onNavigateBack)
            Text(
                "Orders",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.weight(1f))
            OrderSearchBar(
                query = query,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier.weight(1.5f)
            )
        }

        Row(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Column(modifier = Modifier.weight(2.5f)) {
                OrderTableHeader(
                    weights = OrderTableWeights(
                        orderIdWeight,
                        clientNameWeight,
                        dateWeight,
                        quantityWeight,
                        amountWeight
                    ),
                    sortColumn = sortColumn,
                    sortDirection = sortDirection,
                    onSortClick = onSortClick
                )
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
                if (orders.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                        state = listState
                    ) {
                        items(orders, key = { it.id }) { order ->
                            OrderItemRow(
                                order = order,
                                weights = OrderTableWeights(
                                    orderIdWeight,
                                    clientNameWeight,
                                    dateWeight,
                                    quantityWeight,
                                    amountWeight
                                ),
                                onNavigateToOrderDetail = onNavigateToOrderDetail
                            )
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No orders found.", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            Spacer(Modifier.width(24.dp))
            Surface(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Text("Filters", style = MaterialTheme.typography.titleLarge) }
                    item {
                        Text("Total Amount (€)", style = MaterialTheme.typography.titleSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = minAmountInput,
                                onValueChange = {
                                    minAmountInput = it; onMinTotalAmountChange(it.toDoubleOrNull())
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Min") })
                            OutlinedTextField(
                                value = maxAmountInput,
                                onValueChange = {
                                    maxAmountInput = it; onMaxTotalAmountChange(it.toDoubleOrNull())
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Max") })
                        }
                    }
                    item {
                        Text("Total Quantity", style = MaterialTheme.typography.titleSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = minQuantityInput,
                                onValueChange = {
                                    minQuantityInput =
                                        it; onMinTotalQuantityChange(it.toIntOrNull())
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Min") })
                            OutlinedTextField(
                                value = maxQuantityInput,
                                onValueChange = {
                                    maxQuantityInput =
                                        it; onMaxTotalQuantityChange(it.toIntOrNull())
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Max") })
                        }
                    }
                    item {
                        Text("Order Date", style = MaterialTheme.typography.titleSmall)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = formatInstant(startDate),
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Start Date") },
                                placeholder = { Text("YYYY-MM-DD") },
                                readOnly = true,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Select Start Date"
                                    )
                                },
                                trailingIcon = {
                                    if (startDate != null) {
                                        IconButton(onClick = { onStartDateChange(null) }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear Start Date"
                                            )
                                        }
                                    }
                                },
                                interactionSource = remember { MutableInteractionSource() }
                                    .also { interactionSource ->
                                        LaunchedEffect(interactionSource) {
                                            interactionSource.interactions.collect {
                                                if (it is PressInteraction.Release) {
                                                    showStartDatePicker = true
                                                }
                                            }
                                        }
                                    }
                            )

                            OutlinedTextField(
                                value = formatInstant(endDate),
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("End Date") },
                                placeholder = { Text("YYYY-MM-DD") },
                                readOnly = true,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Select End Date"
                                    )
                                },
                                trailingIcon = {
                                    if (endDate != null) {
                                        IconButton(onClick = { onEndDateChange(null) }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear End Date"
                                            )
                                        }
                                    }
                                },
                                interactionSource = remember { MutableInteractionSource() }
                                    .also { interactionSource ->
                                        LaunchedEffect(interactionSource) {
                                            interactionSource.interactions.collect {
                                                if (it is PressInteraction.Release) {
                                                    showEndDatePicker = true
                                                }
                                            }
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class OrderTableWeights(
    val id: Float,
    val client: Float,
    val date: Float,
    val qty: Float,
    val amount: Float
)

@Composable
private fun OrderTableHeader(
    weights: OrderTableWeights,
    sortColumn: SortColumn,
    sortDirection: SortDirection,
    onSortClick: (SortColumn) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell("Order ID", weights.id, false)
        HeaderCell("Client", weights.client, false)
        HeaderCell(
            "Date",
            weights.date,
            true,
            sortColumn == SortColumn.ORDER_DATE,
            sortDirection
        ) { onSortClick(SortColumn.ORDER_DATE) }
        HeaderCell(
            "Quantity",
            weights.qty,
            true,
            sortColumn == SortColumn.TOTAL_QUANTITY,
            sortDirection,
            alignment = TextAlign.Center
        ) { onSortClick(SortColumn.TOTAL_QUANTITY) }
        HeaderCell(
            "Amount",
            weights.amount,
            true,
            sortColumn == SortColumn.TOTAL_AMOUNT,
            sortDirection
        ) { onSortClick(SortColumn.TOTAL_AMOUNT) }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OrderItemRow(
    order: Order,
    weights: OrderTableWeights,
    onNavigateToOrderDetail: (String) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val totalQuantity = remember(order.lines) { order.lines.sumOf { it.quantity } }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToOrderDetail(order.id) }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DataCell(modifier = Modifier.weight(weights.id)) {
            TooltipArea(tooltip = {
                Surface(
                    modifier = Modifier.shadow(4.dp),
                    shape = MaterialTheme.shapes.small
                ) { Text(order.id, Modifier.padding(10.dp)) }
            }) {
                Text(order.id, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        DataCell(modifier = Modifier.weight(weights.client)) {
            Text(order.clientName, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        DataCell(modifier = Modifier.weight(weights.date)) {
            Text(dateFormatter.format(Date.from(order.orderDate.toJavaInstant())), maxLines = 1)
        }
        DataCell(modifier = Modifier.weight(weights.qty), alignment = Alignment.Center) {
            Text("$totalQuantity")
        }
        DataCell(modifier = Modifier.weight(weights.amount)) {
            Text("€%.2f".format(order.totalAmount))
        }
    }
}

@Composable
private fun RowScope.HeaderCell(
    text: String,
    weight: Float,
    isSortable: Boolean,
    isSorted: Boolean = false,
    sortDirection: SortDirection = SortDirection.ASCENDING,
    alignment: TextAlign = TextAlign.Start,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .weight(weight)
            .padding(horizontal = 8.dp),
        contentAlignment = when (alignment) {
            TextAlign.Center -> Alignment.Center
            TextAlign.End -> Alignment.CenterEnd
            else -> Alignment.CenterStart
        }
    ) {
        Row(
            modifier = Modifier.then(
                if (isSortable) Modifier.clip(MaterialTheme.shapes.small)
                    .clickable(onClick = onClick) else Modifier
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (alignment == TextAlign.Center) Arrangement.Center else Arrangement.Start
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = alignment,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isSortable && isSorted) {
                val sortIcon =
                    if (sortDirection == SortDirection.ASCENDING) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
                Icon(
                    imageVector = sortIcon,
                    contentDescription = "Sort Direction",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp).padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun RowScope.DataCell(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.CenterStart,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.padding(horizontal = 8.dp),
        contentAlignment = alignment
    ) {
        content()
    }
}

@Composable
private fun OrderSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Search by Order ID, Client, Product...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        modifier = modifier.height(IntrinsicSize.Min),
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge
    )
}