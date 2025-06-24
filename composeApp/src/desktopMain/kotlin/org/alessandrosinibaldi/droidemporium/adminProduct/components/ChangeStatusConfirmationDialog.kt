package org.alessandrosinibaldi.droidemporium.adminProduct.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.alessandrosinibaldi.droidemporium.commonProduct.domain.Product

@Composable
fun ChangeStatusConfirmationDialog(
    product: Product,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isActivating = !product.isActive
    val title: String
    val message: String
    val confirmButtonText: String
    val confirmButtonColors = if (isActivating) {
        title = "Confirm Activation"
        message = "Are you sure you want to set the product '${product.name}' to Active?"
        confirmButtonText = "Activate"
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    } else {
        title = "Confirm Deactivation"
        message = "Are you sure you want to set the product '${product.name}' to Inactive?"
        confirmButtonText = "Deactivate"
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = confirmButtonColors
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}