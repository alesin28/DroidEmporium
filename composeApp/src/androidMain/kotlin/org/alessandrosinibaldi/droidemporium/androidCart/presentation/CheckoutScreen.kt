package org.alessandrosinibaldi.droidemporium.androidCart.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddAddress: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val viewModel: CheckoutViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refreshAddresses()
    }

    LaunchedEffect(uiState.orderSuccess) {
        if (uiState.orderSuccess) {
            viewModel.onOrderSuccessNavigated()
            onNavigateToHome()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Button(
                    onClick = viewModel::placeOrder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    enabled = uiState.selectedAddress != null &&
                            uiState.cartItems.isNotEmpty() &&
                            !uiState.isPlacingOrder
                ) {
                    if (uiState.isPlacingOrder) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Confirm Order - €${"%.2f".format(uiState.totalAmount)}")
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "Shipping Address",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    if (uiState.addresses.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToAddAddress() },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    "No address found. Tap to add one.",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else {
                        uiState.selectedAddress?.let { address ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "${address.name} ${address.surname}",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(address.street)
                                    Text("${address.city}, ${address.province}, ${address.postalCode}")
                                    Text(address.country)

                                    Spacer(Modifier.height(12.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        if (uiState.addresses.size > 1) {
                                            OutlinedButton(
                                                onClick = viewModel::showAddressSelector
                                            ) {
                                                Text("Change")
                                            }
                                        }
                                        TextButton(onClick = onNavigateToAddAddress) {
                                            Text("Add New")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item { HorizontalDivider(Modifier.padding(vertical = 8.dp)) }

                item {
                    Text(
                        "Order Summary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(uiState.cartItems) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${item.quantity}x ${item.productName}",
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "€${"%.2f".format(item.productPrice * item.quantity)}",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    if (uiState.isAddressSelectorOpen) {
        AlertDialog(
            onDismissRequest = viewModel::hideAddressSelector,
            title = { Text("Select Shipping Address") },
            text = {
                LazyColumn {
                    items(uiState.addresses) { address ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectAddress(address)
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = address.id == uiState.selectedAddress?.id,
                                onClick = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(address.label, fontWeight = FontWeight.Bold)
                                Text(
                                    "${address.street}, ${address.city}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::hideAddressSelector) { Text("Close") }
            }
        )
    }
}