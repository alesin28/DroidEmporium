package org.alessandrosinibaldi.droidemporium.adminClient.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.alessandrosinibaldi.droidemporium.adminClient.presentation.ClientListViewModel.SortColumn
import org.alessandrosinibaldi.droidemporium.adminClient.presentation.ClientListViewModel.SortDirection
import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client
import org.alessandrosinibaldi.droidemporium.core.components.MenuReturnButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientListScreen(
    viewModel: ClientListViewModel = koinViewModel(),
    navController: NavHostController
) {
    val clients by viewModel.clients.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val active by viewModel.isActiveFilter.collectAsState()
    val inactive by viewModel.isInactiveFilter.collectAsState()
    val sortColumn by viewModel.sortColumn.collectAsState()
    val sortDirection by viewModel.sortDirection.collectAsState()

    val onNavigateBack: () -> Unit = {
        navController.popBackStack()
    }

    clientListScreenContent(
        clients = clients,
        query = query,
        active = active,
        inactive = inactive,
        sortColumn = sortColumn,
        sortDirection = sortDirection,
        onClientSearch = viewModel::updateQuery,
        onSortClick = viewModel::updateSort,
        onActiveFilterChange = viewModel::updateActiveFilter,
        onInactiveFilterChange = viewModel::updateInactiveFilter,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun clientListScreenContent(
    clients: List<Client>,
    query: String,
    active: Boolean,
    inactive: Boolean,
    sortColumn: SortColumn,
    sortDirection: SortDirection,
    onClientSearch: (String) -> Unit,
    onSortClick: (SortColumn) -> Unit,
    onActiveFilterChange: (Boolean) -> Unit,
    onInactiveFilterChange: (Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {
    val nameWeight = 2f
    val emailWeight = 2.5f
    val phoneWeight = 1.5f
    val statusWeight = 1f

    val listState = rememberLazyListState()

    LaunchedEffect(clients) {
        listState.scrollToItem(index = 0)
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
                "Clients",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.weight(1f))
            ClientSearchBar(
                query = query,
                onQueryChange = onClientSearch,
                modifier = Modifier.weight(1.5f)
            )
        }

        Row(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Column(modifier = Modifier.weight(2.5f)) {
                ClientTableHeader(
                    weights = ClientTableWeights(
                        nameWeight,
                        emailWeight,
                        phoneWeight,
                        statusWeight
                    ),
                    sortColumn = sortColumn,
                    sortDirection = sortDirection,
                    onSortClick = onSortClick
                )
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

                if (clients.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                        state = listState
                    ) {
                        items(clients, key = { it.id }) { client ->
                            ClientItemRow(
                                client = client,
                                weights = ClientTableWeights(
                                    nameWeight,
                                    emailWeight,
                                    phoneWeight,
                                    statusWeight
                                )
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
                        Text("No clients found.", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            Spacer(Modifier.width(24.dp))

            Surface(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Filters", style = MaterialTheme.typography.titleLarge)
                    Column {
                        Text("Status", style = MaterialTheme.typography.titleSmall)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onActiveFilterChange(!active) }) {
                            Checkbox(checked = active, onCheckedChange = onActiveFilterChange)
                            Text("Active")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onInactiveFilterChange(!inactive) }) {
                            Checkbox(checked = inactive, onCheckedChange = onInactiveFilterChange)
                            Text("Inactive")
                        }
                    }
                }
            }
        }
    }
}


private data class ClientTableWeights(
    val name: Float,
    val email: Float,
    val phone: Float,
    val status: Float
)

@Composable
private fun ClientTableHeader(
    weights: ClientTableWeights,
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
        HeaderCell(
            "Display Name",
            weights.name,
            true,
            sortColumn == SortColumn.NAME,
            sortDirection
        ) { onSortClick(SortColumn.NAME) }
        HeaderCell("Email", weights.email, false)
        HeaderCell("Phone", weights.phone, false)
        HeaderCell("Status", weights.status, false, alignment = TextAlign.Center)
    }
}

@Composable
private fun ClientItemRow(client: Client, weights: ClientTableWeights) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DataCell(modifier = Modifier.weight(weights.name)) {
            Text(
                client.displayName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold
            )
        }
        DataCell(modifier = Modifier.weight(weights.email)) {
            Text(client.email, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        DataCell(modifier = Modifier.weight(weights.phone)) {
            Text(client.phoneNumber ?: "N/A", maxLines = 1)
        }
        DataCell(modifier = Modifier.weight(weights.status), alignment = Alignment.Center) {
            Text(
                text = if (client.isActive) "Active" else "Inactive",
                color = if (client.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.6f
                ),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun ClientSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Search by Name or Email...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        modifier = modifier.height(IntrinsicSize.Min),
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge
    )
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
        modifier = Modifier.weight(weight).padding(horizontal = 8.dp),
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