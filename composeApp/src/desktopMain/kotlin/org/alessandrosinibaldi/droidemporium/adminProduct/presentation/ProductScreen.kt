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

    val onNavigateToAddProduct: () -> Unit = {
        navController.navigate(Route.ProductAdd)
    }
    productScreenContent(
        products = products,
        deleteProduct = viewModel::deleteProduct,
        onNavigateToAddProduct = onNavigateToAddProduct,
        onSortClick = viewModel::updateSort
    )
}

@Composable
fun productScreenContent(
    products: List<Product>,
    deleteProduct: (Product) -> Unit,
    onNavigateToAddProduct: () -> Unit,
    onSortClick: (SortColumn) -> Unit
) {
    val nameWeight = 3f
    val descriptionWeight = 3f
    val priceWeight = 1f
    val actionsWeight = 1f

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
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(2f)

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
                }
            }
        }

    }
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
