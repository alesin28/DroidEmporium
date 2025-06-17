package org.alessandrosinibaldi.droidemporium.core.components

import androidx.compose.material.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MenuReturnButton(
    onNavigateBack: () -> Unit,
) {
    Button(onClick = onNavigateBack) {
        Text("Return to menu")
    }
}

