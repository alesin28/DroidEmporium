package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.alessandrosinibaldi.droidemporium.adminProduct.components.ProductItem
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductListViewModel.SortColumn
import org.koin.compose.viewmodel.koinViewModel
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.*
import org.alessandrosinibaldi.droidemporium.app.Route

@Composable
fun ProductScreen(
    viewModel: ProductListViewModel = koinViewModel(),
    navController: NavHostController
) {
    val products by viewModel.products.collectAsState()
    val minPrice by viewModel.minPriceFilter.collectAsState()
    val maxPrice by viewModel.maxPriceFilter.collectAsState()
    val minStock by viewModel.minStockFilter.collectAsState()
    val maxStock by viewModel.maxStockFilter.collectAsState()
    val query by viewModel.searchQuery.collectAsState()

    val onNavigateToAddProduct: () -> Unit = {
        navController.navigate(Route.ProductAdd)
    }
    productScreenContent(
        products = products,
        minPrice = minPrice,
        maxPrice = maxPrice,
        minStock = minStock,
        maxStock = maxStock,
        query = query,
        deleteProduct = viewModel::deleteProduct,
        onNavigateToAddProduct = onNavigateToAddProduct,
        onSortClick = viewModel::updateSort,
        onMinPriceFilterChange = viewModel::updateMinPriceFilter,
        onMaxPriceFilterChange = viewModel::updateMaxPriceFilter,
        onMinStockFilterChange = viewModel::updateMinStockFilter,
        onMaxStockFilterChange = viewModel::updateMaxStockFilter,
        onProductSearch = viewModel::updateQuery
    )
}

@Composable
fun productScreenContent(
    products: List<Product>,
    minPrice: Double,
    maxPrice: Double,
    minStock: Int,
    maxStock: Int,
    query: String,
    deleteProduct: (Product) -> Unit,
    onNavigateToAddProduct: () -> Unit,
    onSortClick: (SortColumn) -> Unit,
    onMinPriceFilterChange: (Double) -> Unit,
    onMaxPriceFilterChange: (Double) -> Unit,
    onMinStockFilterChange: (Int) -> Unit,
    onMaxStockFilterChange: (Int) -> Unit,
    onProductSearch: (String) -> Unit,
) {
    val nameWeight = 3f
    val descriptionWeight = 3f
    val priceWeight = 1f
    val actionsWeight = 1f
    val stockWeight = 1f
    val activeWeight = 1f
    var minPriceInput by remember { mutableStateOf(minPrice.toString()) }
    var maxPriceInput by remember { mutableStateOf(maxPrice.toString()) }
    var minStockInput by remember { mutableStateOf(minStock.toString()) }
    var maxStockInput by remember { mutableStateOf(maxStock.toString()) }

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
                    ProductSearchBar(
                        query = query,
                        onProductSearch = onProductSearch
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
                                text = "Name",
                                weight = nameWeight,
                                isSortable = true,
                                onClick = { onSortClick(SortColumn.NAME) }

                            )
                            VerticalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            TableHeader(
                                text = "Description",
                                weight = descriptionWeight,
                                isSortable = false
                            )
                            VerticalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            TableHeader(
                                text = "Price",
                                weight = priceWeight,
                                isSortable = true,
                                onClick = { onSortClick(SortColumn.PRICE) }
                            )
                            VerticalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            TableHeader(
                                text = "Stock",
                                weight = stockWeight,
                                isSortable = true,
                                onClick = { onSortClick(SortColumn.STOCK) }
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
                            TableHeader(
                                text = "Actions",
                                weight = actionsWeight,
                                isSortable = false
                            )
                        }

                        HorizontalDivider(thickness = 1.dp)
                        if (!products.isEmpty()) {
                            LazyColumn(
                                modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                items(products) { product ->
                                    ProductItem(
                                        product = product,
                                        deleteProduct = deleteProduct
                                    )
                                    HorizontalDivider(thickness = 1.dp)
                                }
                            }
                        } else {
                            Text("No products available")
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Button(onClick = onNavigateToAddProduct) {
                            Text("Add Product")
                        }
                        Text(
                            text = "Filters",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Price",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .padding(8.dp)

                        ) {
                            OutlinedTextField(
                                value = minPriceInput,
                                onValueChange = { newPrice ->
                                    minPriceInput = newPrice
                                    val newPriceDouble = newPrice.toDoubleOrNull()
                                    if (newPriceDouble != null) {
                                        onMinPriceFilterChange(newPriceDouble)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Min Price") },
                            )
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .weight(0.1f),
                            )
                            OutlinedTextField(
                                value = maxPriceInput,
                                onValueChange = { newPrice ->
                                    maxPriceInput = newPrice
                                    val newPriceDouble = newPrice.toDoubleOrNull()
                                    if (newPriceDouble != null) {
                                        onMaxPriceFilterChange(newPriceDouble)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Max Price") }
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .padding(8.dp)

                        ) {
                            OutlinedTextField(
                                value = minStockInput,
                                onValueChange = { newStock ->
                                    minStockInput = newStock
                                    val newStockInt = newStock.toIntOrNull()
                                    if (newStockInt != null) {
                                        onMinStockFilterChange(newStockInt)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Min Stock") },
                            )
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .weight(0.1f),
                            )
                            OutlinedTextField(
                                value = maxStockInput,
                                onValueChange = { newStock ->
                                    maxStockInput = newStock
                                    val newStockInt = newStock.toIntOrNull()
                                    if (newStockInt != null) {
                                        onMaxStockFilterChange(newStockInt)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Max Stock") }
                            )
                        }


                    }
                }
            }

        }
    }
}

@Composable()
fun ProductSearchBar(onProductSearch: (String) -> Unit, query: String) {
    var productQuery by remember { mutableStateOf(query) }

    OutlinedTextField(
        value = productQuery,
        onValueChange = { newQuery ->
            productQuery = newQuery

            onProductSearch(productQuery)
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
