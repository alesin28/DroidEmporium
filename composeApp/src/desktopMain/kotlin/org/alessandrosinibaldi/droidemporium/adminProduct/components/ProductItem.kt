package org.alessandrosinibaldi.droidemporium.adminProduct.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import org.alessandrosinibaldi.droidemporium.adminProduct.domain.Product
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.alessandrosinibaldi.droidemporium.adminCategory.domain.Category

@Composable
fun ProductItem(
    product: Product,
    category: Category,
    deleteProduct: (Product) -> Unit,
    editProduct: (String) -> Unit,
    onNavigateToProductDetail: (String) -> Unit
) {
    val nameWeight = 3f
    val categoryWeight = 1f
    val priceWeight = 1f
    val stockWeight = 1f
    val activeWeight = 1f
    val actionsWeight = 1f


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(8.dp)
            .background(color = MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp)
                .weight(nameWeight)
                .clickable { onNavigateToProductDetail(product.id.toString()) },
            text = product.name,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        VerticalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp)
                .weight(categoryWeight),
            text = category.name,
            maxLines = 2,
        )
        VerticalDivider(
            thickness = 1.dp
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp)
                .weight(priceWeight),
            text = "${product.price} €",
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        VerticalDivider(
            thickness = 1.dp
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp)
                .weight(stockWeight),
            text = "${product.stock}",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        VerticalDivider(
            thickness = 1.dp
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp)
                .weight(activeWeight),
            text = if (product.isActive) "Active" else "Inactive",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        VerticalDivider(
            thickness = 1.dp
        )
        Column(modifier = Modifier.weight(actionsWeight)) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { deleteProduct(product) }
            ) {
                Text(text = "Delete")
            }
            Button(
                modifier = Modifier.fillMaxWidth(),

                onClick = { editProduct(product.id.toString()) }
            ) {
                Text(text = "Edit")
            }
        }


    }
}