package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import org.alessandrosinibaldi.droidemporium.adminProduct.components.ProductItemWeights
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductListViewModel.SortDirection
import androidx.compose.foundation.lazy.rememberLazyListState
import org.alessandrosinibaldi.droidemporium.adminProduct.components.ChangeStatusConfirmationDialog
import org.alessandrosinibaldi.droidemporium.core.components.MenuReturnButton

@Composable
fun ProductScreen(
    viewModel: ProductListViewModel = koinViewModel(),
    navController: NavHostController
) {
    val productsWithRatings by viewModel.productsWithRatings.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategoryIds by viewModel.selectedCategoryIds.collectAsState()
    val minPrice by viewModel.minPriceFilter.collectAsState()
    val maxPrice by viewModel.maxPriceFilter.collectAsState()
    val minStock by viewModel.minStockFilter.collectAsState()
    val maxStock by viewModel.maxStockFilter.collectAsState()

    val active by viewModel.isActiveFilter.collectAsState()
    val inactive by viewModel.isInactiveFilter.collectAsState()
    val query by viewModel.searchQuery.collectAsState()

    val sortColumn by viewModel.sortColumn.collectAsState()
    val sortDirection by viewModel.sortDirection.collectAsState()


    val onNavigateToAddProduct: () -> Unit = { navController.navigate(Route.ProductAdd) }
    val onNavigateToEditProduct: (String) -> Unit =
        { productId -> navController.navigate(Route.ProductEdit(productId = productId)) }
    val onNavigateToProductDetail: (String) -> Unit =
        { productId -> navController.navigate(Route.ProductDetail(productId = productId)) }

    val onNavigateBack: () -> Unit = {
        navController.popBackStack()
    }

    productScreenContent(
        productsWithRatings = productsWithRatings,
        categories = categories,
        selectedCategoryIds = selectedCategoryIds,
        minPrice = minPrice,
        maxPrice = maxPrice,
        minStock = minStock,
        maxStock = maxStock,
        active = active,
        inactive = inactive,
        query = query,
        changeProductStatus = viewModel::changeProductStatus,
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
        onProductSearch = viewModel::updateQuery,
        sortColumn = sortColumn,
        sortDirection = sortDirection,
        onNavigateBack = onNavigateBack
    )
}


@Composable
fun productScreenContent(
    productsWithRatings: List<ProductListViewModel.ProductWithAverageRating>,
    categories: List<Category>,
    selectedCategoryIds: Set<String>,
    minPrice: Double?,
    maxPrice: Double?,
    minStock: Int?,
    maxStock: Int?,
    active: Boolean,
    inactive: Boolean,
    query: String,
    changeProductStatus: (Product) -> Unit,
    onNavigateToAddProduct: () -> Unit,
    onNavigateToEditProduct: (String) -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    onSortClick: (SortColumn) -> Unit,
    onMinPriceFilterChange: (Double?) -> Unit,
    onMaxPriceFilterChange: (Double?) -> Unit,
    onMinStockFilterChange: (Int?) -> Unit,
    onMaxStockFilterChange: (Int?) -> Unit,
    onActiveFilterChange: (Boolean) -> Unit,
    onInactiveFilterChange: (Boolean) -> Unit,
    onCategorySelectionChange: (String, Boolean) -> Unit,
    onProductSearch: (String) -> Unit,
    sortColumn: SortColumn,
    sortDirection: SortDirection,
    onNavigateBack: () -> Unit
) {
    val nameWeight = 2.5f
    val categoryWeight = 1.5f
    val priceWeight = 1f
    val stockWeight = 1f
    val ratingWeight = 1f
    val activeWeight = 1f
    val actionsWeight = 1.5f

    val listState = rememberLazyListState()

    var productForStatusChange by remember { mutableStateOf<Product?>(null) }

    var minPriceInput by remember { mutableStateOf(minPrice?.toString() ?: "") }
    var maxPriceInput by remember { mutableStateOf(maxPrice?.toString() ?: "") }
    var minStockInput by remember { mutableStateOf(minStock?.toString() ?: "") }
    var maxStockInput by remember { mutableStateOf(maxStock?.toString() ?: "") }

    LaunchedEffect(productsWithRatings) {
        if (listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0) {
            listState.scrollToItem(index = 0)
        }
    }

    LaunchedEffect(minPrice) { minPriceInput = minPrice?.toString() ?: "" }
    LaunchedEffect(maxPrice) { maxPriceInput = maxPrice?.toString() ?: "" }
    LaunchedEffect(minStock) { minStockInput = minStock?.toString() ?: "" }
    LaunchedEffect(maxStock) { maxStockInput = maxStock?.toString() ?: "" }

    productForStatusChange?.let { product ->
        ChangeStatusConfirmationDialog(
            product = product,
            onConfirm = {
                changeProductStatus(product)
                productForStatusChange = null
            },
            onDismiss = {
                productForStatusChange = null
            }
        )
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
                "Products",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.weight(1f))
            ProductSearchBar(
                query = query,
                onProductSearch = onProductSearch,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = onNavigateToAddProduct) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Product",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Add Product")
            }
        }

        Row(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Column(modifier = Modifier.weight(2.5f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableHeader(
                        "Name",
                        nameWeight,
                        true,
                        sortColumn == SortColumn.NAME,
                        sortDirection
                    ) { onSortClick(SortColumn.NAME) }
                    TableHeader("Category", categoryWeight, false)
                    TableHeader(
                        "Price",
                        priceWeight,
                        true,
                        sortColumn == SortColumn.PRICE,
                        sortDirection
                    ) { onSortClick(SortColumn.PRICE) }
                    TableHeader(
                        "Stock",
                        stockWeight,
                        true,
                        sortColumn == SortColumn.STOCK,
                        sortDirection
                    ) { onSortClick(SortColumn.STOCK) }
                    TableHeader(
                        "Rating",
                        ratingWeight,
                        true,
                        sortColumn == SortColumn.AVERAGE_RATING,
                        sortDirection
                    ) { onSortClick(SortColumn.AVERAGE_RATING) }
                    TableHeader("Status", activeWeight, false)
                    TableHeader("Actions", actionsWeight, false, alignment = TextAlign.Center)
                }
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

                if (productsWithRatings.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                        state = listState
                    ) {
                        items(productsWithRatings, key = { it.product.id }) { productWithRating ->
                            val category =
                                categories.find { it.id == productWithRating.product.categoryId }
                            if (category != null) {
                                ProductItem(
                                    product = productWithRating.product,
                                    category = category,
                                    averageRating = productWithRating.averageRating,
                                    onStatusChangeRequest = { productForStatusChange = it },
                                    editProduct = onNavigateToEditProduct,
                                    onNavigateToProductDetail = onNavigateToProductDetail,
                                    weights = ProductItemWeights(
                                        nameWeight,
                                        categoryWeight,
                                        priceWeight,
                                        stockWeight,
                                        ratingWeight,
                                        activeWeight,
                                        actionsWeight
                                    )
                                )
                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No products found.", style = MaterialTheme.typography.bodyLarge)
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            "Filters",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    item {
                        Text("Price Range (€)", style = MaterialTheme.typography.titleSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = minPriceInput,
                                onValueChange = {
                                    minPriceInput = it
                                    onMinPriceFilterChange(it.toDoubleOrNull())
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Min") },
                            )
                            OutlinedTextField(
                                value = maxPriceInput,
                                onValueChange = {
                                    maxPriceInput = it
                                    onMaxPriceFilterChange(it.toDoubleOrNull())
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Max") }
                            )
                        }
                    }
                    item {
                        Text("Stock Quantity", style = MaterialTheme.typography.titleSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = minStockInput,
                                onValueChange = {
                                    minStockInput = it
                                    onMinStockFilterChange(it.toIntOrNull())
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Min") },
                            )
                            OutlinedTextField(
                                value = maxStockInput,
                                onValueChange = {
                                    maxStockInput = it
                                    onMaxStockFilterChange(it.toIntOrNull())
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Max") }
                            )
                        }
                    }

                    item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

                    item {
                        Text("Status", style = MaterialTheme.typography.titleSmall)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = active, onCheckedChange = onActiveFilterChange)
                            Text("Active")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = inactive, onCheckedChange = onInactiveFilterChange)
                            Text("Inactive")
                        }
                    }

                    item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

                    item {
                        Text("Category", style = MaterialTheme.typography.titleSmall)
                    }
                    items(categories) { category ->
                        val categoryId = category.id
                        val isSelected = selectedCategoryIds.contains(categoryId)
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { onCategorySelectionChange(categoryId, !isSelected) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { onCategorySelectionChange(categoryId, it) })
                            Text(text = category.name, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ProductSearchBar(
    onProductSearch: (String) -> Unit,
    query: String,
    modifier: Modifier = Modifier
) {

    OutlinedTextField(
        value = query,
        onValueChange = onProductSearch,
        label = { Text("Search Products...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        modifier = modifier.height(IntrinsicSize.Min),
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge
    )
}

@Composable
fun RowScope.TableHeader(
    text: String,
    weight: Float,
    isSortable: Boolean,
    isSorted: Boolean = false,
    sortDirection: SortDirection = SortDirection.ASCENDING,
    alignment: TextAlign = TextAlign.Start,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .weight(weight)
            .then(
                if (isSortable) Modifier.clip(MaterialTheme.shapes.small)
                    .clickable(onClick = onClick) else Modifier
            )
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (alignment == TextAlign.Center) Arrangement.Center else Arrangement.Start
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = alignment,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = if (alignment == TextAlign.Start) 8.dp else 0.dp)
        )
        if (isSortable && isSorted) {
            Icon(
                imageVector = if (sortDirection == SortDirection.ASCENDING) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = "Sort Direction",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp).padding(start = 4.dp)
            )
        }
    }
}