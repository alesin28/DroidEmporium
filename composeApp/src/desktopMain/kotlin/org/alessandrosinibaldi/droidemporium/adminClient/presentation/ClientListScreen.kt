package org.alessandrosinibaldi.droidemporium.adminClient.presentation

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
import org.alessandrosinibaldi.droidemporium.app.Route
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
    val sortColumn by viewModel.sortColumn.collectAsState()
    val sortDirection by viewModel.sortDirection.collectAsState()

    val onNavigateBack: () -> Unit = {
        navController.popBackStack()
    }
    val onNavigateToClientDetail: (String) -> Unit = { clientId ->
        navController.navigate(Route.ClientDetail(clientId = clientId))
    }

    clientListScreenContent(
        clients = clients,
        query = query,
        sortColumn = sortColumn,
        sortDirection = sortDirection,
        onClientSearch = viewModel::updateQuery,
        onSortClick = viewModel::updateSort,
        onNavigateBack = onNavigateBack,
        onNavigateToClientDetail = onNavigateToClientDetail
    )
}

@Composable
fun clientListScreenContent(
    clients: List<Client>,
    query: String,
    sortColumn: SortColumn,
    sortDirection: SortDirection,
    onClientSearch: (String) -> Unit,
    onSortClick: (SortColumn) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToClientDetail: (String) -> Unit
) {
    val nameWeight = 2.5f
    val emailWeight = 3f
    val phoneWeight = 1.5f

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
                        phoneWeight
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
                                    phoneWeight
                                ),
                                onClick = { onNavigateToClientDetail(client.id) }
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


        }
    }
}


private data class ClientTableWeights(
    val name: Float,
    val email: Float,
    val phone: Float
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
    }
}

@Composable
private fun ClientItemRow(client: Client, weights: ClientTableWeights, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        DataCell(modifier = Modifier.weight(weights.name)) {
            Text(
                client.displayName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        DataCell(modifier = Modifier.weight(weights.email)) {
            Text(client.email, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        DataCell(modifier = Modifier.weight(weights.phone)) {
            Text(client.phoneNumber ?: "N/A", maxLines = 1)
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