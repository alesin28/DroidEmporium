package org.alessandrosinibaldi.droidemporium.adminCategory.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import org.alessandrosinibaldi.droidemporium.app.Route
import org.alessandrosinibaldi.droidemporium.core.components.MenuReturnButton

@Composable
fun CategoryListScreen(
    viewModel: CategoryListViewModel = koinViewModel(),
    navController: NavHostController
) {
    val categories by viewModel.categories.collectAsState()
    val products by viewModel.products.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val onProductClick: (String) -> Unit = { productId ->
        navController.navigate(Route.ProductDetail(productId = productId))
    }

    val onNavigateBack: () -> Unit = {
        navController.popBackStack()
    }

    CategoryListScreenContent(
        categories = categories,
        products = products,
        searchQuery = searchQuery,
        onCategorySearch = viewModel::updateSearchQuery,
        onProductClick = onProductClick,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun CategoryListScreenContent(
    categories: List<Category>,
    products: List<Product>,
    searchQuery: String,
    onCategorySearch: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var expandedCategoryId by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Use theme color
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MenuReturnButton(onNavigateBack = onNavigateBack)
                CategorySearchBar(
                    query = searchQuery,
                    onQueryChange = onCategorySearch,
                    modifier = Modifier.weight(1f).padding(horizontal = 24.dp)
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories, key = { it.id }) { category ->
                    ExpandableCategoryItem(
                        category = category,
                        products = products.filter { it.categoryId == category.id },
                        isExpanded = expandedCategoryId == category.id,
                        onHeaderClick = {
                            expandedCategoryId = if (expandedCategoryId == category.id) null else category.id
                        },
                        onProductClick = onProductClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandableCategoryItem(
    category: Category,
    products: List<Product>,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val arrowRotationDegrees by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if(isExpanded) 4.dp else 1.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onHeaderClick)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${products.size} products",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(arrowRotationDegrees),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    if (products.isEmpty()) {
                        Text(
                            text = "No products in this category.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                        )
                    } else {
                        products.forEach { product ->
                            ProductRow(
                                product = product,
                                onClick = { onProductClick(product.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductRow(
    product: Product,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = product.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Stock: ${product.stock}",
            style = MaterialTheme.typography.bodyMedium,
            color = if (product.stock < 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CategorySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Search Categories...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        modifier = modifier,
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge
    )
}