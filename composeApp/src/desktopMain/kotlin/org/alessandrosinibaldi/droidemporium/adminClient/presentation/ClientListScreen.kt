package org.alessandrosinibaldi.droidemporium.adminClient.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.*
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.alessandrosinibaldi.droidemporium.adminClient.components.ClientItem
import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client
import org.alessandrosinibaldi.droidemporium.adminClient.presentation.ClientListViewModel.SortColumn
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientListScreen(
    viewModel: ClientListViewModel = koinViewModel(),
    navController: NavHostController
) {

    val clients by viewModel.clients.collectAsState()
    val active by viewModel.isActiveFilter.collectAsState()
    val inactive by viewModel.isInactiveFilter.collectAsState()
    val query by viewModel.searchQuery.collectAsState()

    clientScreenContent(
        clients = clients,
        active = active,
        inactive = inactive,
        query = query,
        onSortClick = viewModel::updateSort,
        onActiveFilterChange = viewModel::updateActiveFilter,
        onInactiveFilterChange = viewModel::updateInactiveFilter,
        onClientSearch = viewModel::updateQuery
    )
}

@Composable
fun clientScreenContent(
    clients: List<Client>,
    active: Boolean,
    inactive: Boolean,
    query: String,
    onSortClick: (SortColumn) -> Unit,
    onActiveFilterChange: (Boolean) -> Unit,
    onInactiveFilterChange: (Boolean) -> Unit,
    onClientSearch: (String) -> Unit
) {
    val nameWeight = 1f
    val emailWeight = 2f
    val phoneWeight = 2f
    val activeWeight = 1f

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFADD8E6)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {

                Row {
                    ClientSearchBar(
                        query = query,
                        onProductSearch = onClientSearch
                    )
                }

                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(3f)

                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = Color.LightGray)
                                .padding(8.dp)
                                .height(IntrinsicSize.Min)
                        ) {
                            TableHeader(
                                text = "Display Name",
                                weight = nameWeight,
                                isSortable = true,
                                onClick = { onSortClick(SortColumn.NAME) }

                            )
                            VerticalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            TableHeader(
                                text = "Email",
                                weight = emailWeight,
                                isSortable = false
                            )
                            VerticalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            TableHeader(
                                text = "Phone",
                                weight = phoneWeight,
                                isSortable = false
                            )
                            VerticalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            TableHeader(
                                text = "Status",
                                weight = activeWeight,
                                isSortable = false
                            )
                            VerticalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        HorizontalDivider(thickness = 1.dp)
                        if (!clients.isEmpty()) {
                            LazyColumn(
                                modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                items(clients) { client ->

                                    ClientItem(
                                        client = client
                                    )
                                    HorizontalDivider(thickness = 1.dp)
                                }
                            }

                        } else {
                            Text("No Clients available")
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Filters",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Row {
                            Text("Show Active Clients")
                            Checkbox(
                                checked = active,
                                onCheckedChange = { newState ->
                                    if (!active && inactive || (active && inactive)) {
                                        onActiveFilterChange(newState)
                                    }
                                }
                            )
                        }
                        Row {
                            Text("Show Inactive Clients")
                            Checkbox(
                                checked = inactive,
                                onCheckedChange = { newState ->
                                    if (!inactive && active || active) {
                                        onInactiveFilterChange(newState)
                                    }
                                }
                            )
                        }

                        //LazyColumn {
                        //    items(categories) {category ->
                        //        Text(text = category.name)

                        //    }
                        //}


                    }
                }
            }

        }
    }
}


@Composable()
fun ClientSearchBar(onProductSearch: (String) -> Unit, query: String) {
    var clientQuery by remember { mutableStateOf(query) }

    OutlinedTextField(
        value = clientQuery,
        onValueChange = { newQuery ->
            clientQuery = newQuery

            onProductSearch(clientQuery)
        },
        label = { Text("Search") },
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 50.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)


    )
}

@Composable
fun RowScope.TableHeader(
    text: String,
    weight: Float,
    isSortable: Boolean,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .weight(weight)
            .then(if (isSortable) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )

    }


}
