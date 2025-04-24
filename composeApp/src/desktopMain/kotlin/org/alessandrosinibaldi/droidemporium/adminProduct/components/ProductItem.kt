package org.alessandrosinibaldi.droidemporium.adminProduct.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.Product
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProductItem(
    product: Product,
    deleteProduct: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)

        ) {
            Text(
                text = "Name: ${product.name}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Price: ${product.price}€",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = { deleteProduct(product) }
            ) {
                Text(text = "Delete")
            }
        }
    }
}