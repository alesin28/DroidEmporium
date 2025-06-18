package org.alessandrosinibaldi.droidemporium.adminProduct.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ProductDetailScreen(productId: String?) {

    val viewModel: ProductDetailViewModel = koinViewModel(
        parameters = { parametersOf(productId) }
    )

    val category by viewModel.category.collectAsState()
    val product by viewModel.product.collectAsState()

    ProductDetailScreenContent(
        category = category,
        product = product
    )

}

@Composable
fun ProductDetailScreenContent(
    category: Category?,
    product: Product?
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Text("ID: ${product?.id}")
                Text("Name: ${product?.name}")
                Text("Description: ${product?.description}")
                Text("Stock: ${product?.stock}")
                Text("Category: ${category?.name}")
                if(product?.isActive == true){
                    Text("Status: Active")
                } else {
                    Text("Status: Inactive")
                }
            }
        }
}
}