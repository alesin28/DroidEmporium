package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.*
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.Product
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import org.alessandrosinibaldi.droidemporium.adminProduct.components.ProductItem
import org.alessandrosinibaldi.droidemporium.app.Route
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductScreen(
    viewModel: ProductListViewModel = koinViewModel(),
    navController: NavHostController
) {
    val products by viewModel.products.collectAsState()
    val onNavigateToAddProduct: () -> Unit = {
        navController.navigate(Route.ProductAdd)
    }
    productScreenContent(
        products = products,
        deleteProduct = viewModel::deleteProduct,
        onNavigateToAddProduct = onNavigateToAddProduct
    )
}

@Composable
fun productScreenContent(
    products: List<Product>,
    deleteProduct: (Product) -> Unit,
    onNavigateToAddProduct: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFADD8E6)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.weight(2f)

                ) {
                    if (!products.isEmpty()) {
                        LazyColumn {
                            items(products) { product ->
                                ProductItem(
                                    product = product,
                                    deleteProduct = deleteProduct
                                )
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
                }
            }
        }

    }
}