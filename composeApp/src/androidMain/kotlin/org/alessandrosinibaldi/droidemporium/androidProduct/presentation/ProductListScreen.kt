package org.alessandrosinibaldi.droidemporium.androidProduct.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.alessandrosinibaldi.droidemporium.app.Route
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.core.components.ProductCard
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ProductListScreen(
    navController: NavHostController,
    routeArgs: Route.ProductList
) {
    val viewModel: ProductListViewModel = koinViewModel(
        parameters = {
            parametersOf(
                routeArgs.categoryId,
                routeArgs.categoryName,
                routeArgs.showNewest,
                routeArgs.query
            )
        }
    )

    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val screenTitle by viewModel.screenTitle.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    ProductListScreenContent(
        navController = navController,
        screenTitle = screenTitle,
        startSearch = routeArgs.startSearch,
        products = products,
        isLoading = isLoading,
        searchQuery = searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSortOptionChange = viewModel::updateSortOption
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreenContent(
    navController: NavHostController,
    screenTitle: String,
    startSearch: Boolean,
    products: List<Product>,
    isLoading: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSortOptionChange: (SortOption) -> Unit
) {

    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (startSearch) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp),
                            placeholder = { Text("Search products...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    } else {
                        Text(screenTitle)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Sort Products")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Name (A-Z)") },
                                onClick = {
                                    onSortOptionChange(SortOption.NAME_ASC)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Name (Z-A)") },
                                onClick = {
                                    onSortOptionChange(SortOption.NAME_DESC)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Price (Low to High)") },
                                onClick = {
                                    onSortOptionChange(SortOption.PRICE_ASC)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Price (High to Low)") },
                                onClick = {
                                    onSortOptionChange(SortOption.PRICE_DESC)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (products.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                val message = if (startSearch && searchQuery.isBlank()) {
                    "Start typing to search for products."
                } else {
                    "No products found."
                }
                Text(message, style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onProductClick = { productId -> navController.navigate(Route.ProductDetail(productId = productId))})
                }
            }
        }
    }
}