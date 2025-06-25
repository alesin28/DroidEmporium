package org.alessandrosinibaldi.droidemporium.adminOrder.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.datetime.toJavaInstant
import org.alessandrosinibaldi.droidemporium.app.Route
import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.core.components.MenuReturnButton
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderDetailScreen(
    orderId: String?,
    navController: NavHostController
) {
    val viewModel: OrderDetailViewModel = koinViewModel(
        parameters = { parametersOf(orderId) }
    )
    val order by viewModel.order.collectAsState()
    val client by viewModel.client.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    OrderDetailScreenContent(
        order = order,
        client = client,
        isLoading = isLoading,
        onNavigateBack = { navController.popBackStack() },
        onProductClick = { productId ->
            navController.navigate(Route.ProductDetail(productId = productId))
        },
        onClientClick = { clientId ->
            navController.navigate(Route.ClientDetail(clientId = clientId))
        }
    )
}

@Composable
fun OrderDetailScreenContent(
    order: Order?,
    client: Client?,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onClientClick: (String) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (order == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Order not found.", style = MaterialTheme.typography.headlineSmall)
                    MenuReturnButton(onNavigateBack)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().widthIn(max = 1200.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MenuReturnButton(onNavigateBack)
                        Spacer(Modifier.width(16.dp))
                        Text("Order Details", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.height(24.dp))
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().widthIn(max = 1200.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.large,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text("Summary", style = MaterialTheme.typography.titleLarge)
                                HorizontalDivider()
                                Text("Order ID: ${order.id}", style =  MaterialTheme.typography.bodyLarge)
                                Text("Client", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "Name: ${client?.displayName ?: order.clientName}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.clickable { onClientClick(order.clientId) }
                                )
                                Text(
                                    text = "ID: ${client?.id ?: order.clientId}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.clickable { onClientClick(order.clientId) }
                                )
                                Text("Order Date", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(dateFormatter.format(Date.from(order.orderDate.toJavaInstant())), style = MaterialTheme.typography.bodyLarge)
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium).padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Total Amount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("€%.2f".format(order.totalAmount), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1.8f)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Product", Modifier.weight(2.5f), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Qty", Modifier.weight(0.5f), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                Text("Price", Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                                Text("Subtotal", Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                                order.lines.forEach { line ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = line.productName,
                                            modifier = Modifier.weight(2.5f).clickable { onProductClick(line.productId) },
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(line.quantity.toString(), Modifier.weight(0.5f), textAlign = TextAlign.Center)
                                        Text("€%.2f".format(line.priceAtPurchase), Modifier.weight(1f), textAlign = TextAlign.End)
                                        Text("€%.2f".format(line.lineTotal), Modifier.weight(1f), textAlign = TextAlign.End, fontWeight = FontWeight.SemiBold)
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
