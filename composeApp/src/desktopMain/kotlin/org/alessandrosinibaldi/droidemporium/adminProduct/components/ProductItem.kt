package org.alessandrosinibaldi.droidemporium.adminProduct.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.alessandrosinibaldi.droidemporium.commonCategory.domain.Category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.font.FontWeight
import org.alessandrosinibaldi.droidemporium.ui.theme.SuccessGreen

data class ProductItemWeights(
    val name: Float, val category: Float, val price: Float,
    val stock: Float, val active: Float, val actions: Float
)

@Composable
fun ProductItem(
    product: Product,
    category: Category,
    onStatusChangeRequest: (Product) -> Unit,
    editProduct: (String) -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    weights: ProductItemWeights
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToProductDetail(product.id) }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(weights.name).padding(horizontal = 8.dp),
            text = product.name, maxLines = 2, overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = Modifier.weight(weights.category).padding(horizontal = 8.dp),
            text = category.name, maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            modifier = Modifier.weight(weights.price).padding(horizontal = 8.dp),
            text = "€${product.price}", maxLines = 1,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            modifier = Modifier.weight(weights.stock).padding(horizontal = 8.dp),
            text = "${product.stock}", maxLines = 1,
            style = MaterialTheme.typography.bodyMedium,
            color = if (product.stock < 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
        Text(
            modifier = Modifier.weight(weights.active).padding(horizontal = 8.dp),
            text = if (product.isActive) "Active" else "Inactive", maxLines = 1,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = if (product.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Row(
            modifier = Modifier.weight(weights.actions),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { editProduct(product.id) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Product", tint = MaterialTheme.colorScheme.secondary)
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = { onStatusChangeRequest(product) }) {
                if (product.isActive) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Deactivate Product",
                        tint = MaterialTheme.colorScheme.error
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Activate Product",
                        tint = SuccessGreen
                    )
                }
            }
        }
    }
}