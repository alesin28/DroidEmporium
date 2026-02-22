package org.alessandrosinibaldi.droidemporium.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.alessandrosinibaldi.droidemporium.app.Route
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.alessandrosinibaldi.droidemporium.core.components.ProductCard
import org.alessandrosinibaldi.droidemporium.core.components.SearchBar
import org.alessandrosinibaldi.droidemporium.home.components.CategoryCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    navController: NavHostController
) {
    val state by viewModel.uiState.collectAsState()


    val onCategoryClick: (Category) -> Unit = { category ->
        navController.navigate(
            Route.ProductList(
                categoryId = category.id,
                categoryName = category.name,
                showNewest = false,
                query = null,
                startSearch = false
            )
        )
    }

    val onSearchClick: (String) -> Unit = { query ->
        navController.navigate(
            Route.ProductList(
                categoryId = null,
                categoryName = null,
                showNewest = false,
                query = query,
                startSearch = true
            )
        )
    }

    val onSeeAllNewestClick: () -> Unit = {
        navController.navigate(
            Route.ProductList(showNewest = true)
        )
    }
    val onProductClick: (String) -> Unit = { productId ->
        navController.navigate(Route.ProductDetail(productId = productId))
    }
    val onCartClick: () -> Unit = {
        navController.navigate(Route.Cart)
    }

    HomeScreenContent(
        isLoading = state.isLoading,
        categories = state.categories,
        newProducts = state.newProducts,
        onCategoryClick = onCategoryClick,
        onProductClick = onProductClick,
        onSearchClick = onSearchClick,
        onCartClick = onCartClick,
        onSeeAllNewestClick = onSeeAllNewestClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    isLoading: Boolean,
    categories: List<Category>,
    newProducts: List<Product>,
    onCategoryClick: (Category) -> Unit,
    onProductClick: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onSeeAllNewestClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SearchBar(onSearchClicked = onSearchClick)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "View Cart")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "Shop by Category",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.height(((categories.size + 1) / 2 * 150).dp)
                        ) {
                            items(categories, key = { it.id }) { category ->
                                CategoryCard(
                                    category = category,
                                    onCategoryClick = { onCategoryClick(category) }
                                )
                            }
                        }
                    }
                }

                item {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "New Products",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            TextButton(onClick = onSeeAllNewestClick) {
                                Text("See All")
                            }
                        }
                        Spacer(modifier = Modifier.padding(8.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(newProducts, key = { it.id }) { product ->
                                Box(modifier = Modifier.width(200.dp)) {
                                    ProductCard(
                                        product = product,
                                        onProductClick = onProductClick
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