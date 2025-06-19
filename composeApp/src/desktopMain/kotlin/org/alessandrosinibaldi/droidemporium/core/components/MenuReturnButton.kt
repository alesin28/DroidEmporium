package org.alessandrosinibaldi.droidemporium.core.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuReturnButton(
    onNavigateBack: () -> Unit,
) {
    OutlinedButton(onClick = onNavigateBack) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Return",
            tint = MaterialTheme.colorScheme.primary
        )
        Text("Return", modifier = Modifier.padding(start = 8.dp))
    }
}

