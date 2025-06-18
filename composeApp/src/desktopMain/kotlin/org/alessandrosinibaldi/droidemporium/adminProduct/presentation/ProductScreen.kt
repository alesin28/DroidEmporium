package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.adminProduct.components.ProductItem
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductListViewModel.SortColumn
import org.koin.compose.viewmodel.koinViewModel
import org.alessandrosinibaldi.droidemporium.app.Route
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product

@Composable
fun ProductScreen(
    viewModel: ProductListViewModel = koinViewModel(),
    navController: NavHostController
) {
    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategoryIds by viewModel.selectedCategoryIds.collectAsState()
    val minPrice by viewModel.minPriceFilter.collectAsState()
    val maxPrice by viewModel.maxPriceFilter.collectAsState()
    val minStock by viewModel.minStockFilter.collectAsState()
    val maxStock by viewModel.maxStockFilter.collectAsState()
    val active by viewModel.isActiveFilter.collectAsState()
    val inactive by viewModel.isInactiveFilter.collectAsState()
    val query by viewModel.searchQuery.collectAsState()

    val onNavigateToAddProduct: () -> Unit = {
        navController.navigate(Route.ProductAdd)

    }
    val onNavigateToEditProduct: (String) -> Unit = { productId ->
        navController.navigate(Route.ProductEdit(productId = productId))
    }

    val onNavigateToProductDetail: (String) -> Unit = { productId ->
        navController.navigate(Route.ProductDetail(productId = productId))
    }

    productScreenContent(
        products = products,
        categories = categories,
        selectedCategoryIds = selectedCategoryIds,
        minPrice = minPrice,
        maxPrice = maxPrice,
        minStock = minStock,
        maxStock = maxStock,
        active = active,
        inactive = inactive,
        query = query,
        deleteProduct = viewModel::deleteProduct,
        onNavigateToAddProduct = onNavigateToAddProduct,
        onNavigateToEditProduct = onNavigateToEditProduct,
        onNavigateToProductDetail = onNavigateToProductDetail,
        onSortClick = viewModel::updateSort,
        onMinPriceFilterChange = viewModel::updateMinPriceFilter,
        onMaxPriceFilterChange = viewModel::updateMaxPriceFilter,
        onMinStockFilterChange = viewModel::updateMinStockFilter,
        onMaxStockFilterChange = viewModel::updateMaxStockFilter,
        onActiveFilterChange = viewModel::updateActiveFilter,
        onInactiveFilterChange = viewModel::updateInactiveFilter,
        onCategorySelectionChange = viewModel::updateSelectedCategories,
        onProductSearch = viewModel::updateQuery
    )
}

@Composable
fun productScreenContent(
    products: List<Product>,
    categories: List<Category>,
    selectedCategoryIds: Set<String>,
    minPrice: Double,
    maxPrice: Double,
    minStock: Int,
    maxStock: Int,
    active: Boolean,
    inactive: Boolean,
    query: String,
    deleteProduct: (Product) -> Unit,
    onNavigateToAddProduct: () -> Unit,
    onNavigateToEditProduct: (String) -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    onSortClick: (SortColumn) -> Unit,
    onMinPriceFilterChange: (Double) -> Unit,
    onMaxPriceFilterChange: (Double) -> Unit,
    onMinStockFilterChange: (Int) -> Unit,
    onMaxStockFilterChange: (Int) -> Unit,
    onActiveFilterChange: (Boolean) -> Unit,
    onInactiveFilterChange: (Boolean) -> Unit,
    onCategorySelectionChange: (String, Boolean) -> Unit,
    onProductSearch: (String) -> Unit,

    ) {
    val nameWeight = 3f
    val categoryWeight = 1f
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
        color = MaterialTheme.colorScheme.background
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
                                .background(color = MaterialTheme.colorScheme.surfaceVariant)
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
                                color = MaterialTheme.colorScheme.outline
                            )
                            TableHeader(
                                text = "Category",
                                weight = categoryWeight,
                                isSortable = false
                            )
                            VerticalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            TableHeader(
                                text = "Price",
                                weight = priceWeight,
                                isSortable = true,
                                onClick = { onSortClick(SortColumn.PRICE) }
                            )
                            VerticalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            TableHeader(
                                text = "Stock",
                                weight = stockWeight,
                                isSortable = true,
                                onClick = { onSortClick(SortColumn.STOCK) }
                            )
                            VerticalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            TableHeader(
                                text = "Status",
                                weight = activeWeight,
                                isSortable = false
                            )
                            VerticalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            TableHeader(
                                text = "Actions",
                                weight = actionsWeight,
                                isSortable = false
                            )
                        }

                        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
                        if (!products.isEmpty()) {
                            LazyColumn {
                                items(products) { product ->
                                    categories.find { category -> category.id == product.categoryId }
                                        ?.let {
                                            ProductItem(
                                                product = product,
                                                category = it,
                                                deleteProduct = deleteProduct,
                                                editProduct = onNavigateToEditProduct,
                                                onNavigateToProductDetail = onNavigateToProductDetail
                                            )
                                        }
                                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
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

                            Spacer(modifier = Modifier.weight(0.1f))

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
                                color = androidx.compose.ui.graphics.Color.Transparent
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
                        Row {
                            Text("Show Active Products")
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
                            Text("Show Inactive Products")
                            Checkbox(
                                checked = inactive,
                                onCheckedChange = { newState ->
                                    if (!inactive && active || active) {
                                        onInactiveFilterChange(newState)
                                    }
                                }
                            )
                        }

                        Text("Category", style = MaterialTheme.typography.titleMedium)
                        Column(
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            categories.forEach { category ->
                                val categoryId = category.id
                                if (categoryId != null) {
                                    val isSelected = selectedCategoryIds.contains(categoryId)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onCategorySelectionChange(
                                                    categoryId,
                                                    !isSelected
                                                )
                                            }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = { checked ->
                                                onCategorySelectionChange(
                                                    categoryId,
                                                    checked
                                                )
                                            }
                                        )
                                        Text(
                                            text = category.name,
                                            modifier = Modifier.padding(start = 8.dp),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

    }
}