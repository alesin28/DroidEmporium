package org.alessandrosinibaldi.droidemporium.adminClient.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.alessandrosinibaldi.droidemporium.app.Route
import org.alessandrosinibaldi.droidemporium.commonAddress.domain.Address
import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
import org.alessandrosinibaldi.droidemporium.commonReview.domain.Review
import org.alessandrosinibaldi.droidemporium.core.components.MenuReturnButton
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.alessandrosinibaldi.droidemporium.adminClient.components.ClientAddressesCard
import org.alessandrosinibaldi.droidemporium.adminClient.components.ClientInfoCard
import org.alessandrosinibaldi.droidemporium.adminClient.components.ClientOrderItem
import org.alessandrosinibaldi.droidemporium.adminClient.components.ClientReviewItem
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight


@Composable
fun ClientDetailScreen(
    clientId: String?,
    navController: NavHostController
) {
    val viewModel: ClientDetailViewModel = koinViewModel(
        parameters = { parametersOf(clientId) }
    )
    val client by viewModel.client.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val addresses by viewModel.addresses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    ClientDetailScreenContent(
        client = client,
        orders = orders,
        reviews = reviews,
        addresses = addresses,
        isLoading = isLoading,
        onNavigateBack = { navController.popBackStack() },
        onOrderClick = { orderId ->
            navController.navigate(Route.OrderDetail(orderId = orderId))
        },
        onProductClick = { productId ->
            navController.navigate(Route.ProductDetail(productId = productId))
        }
    )
}

@Composable
fun ClientDetailScreenContent(
    client: Client?,
    orders: List<Order>,
    reviews: List<Review>,
    addresses: List<Address>,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onOrderClick: (String) -> Unit,
    onProductClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (client == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Client not found.", style = MaterialTheme.typography.headlineSmall)
                    MenuReturnButton(onNavigateBack)
                }
            }
        } else {
            var isOrdersExpanded by remember { mutableStateOf(true) }
            var isReviewsExpanded by remember { mutableStateOf(true) }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().widthIn(max = 1200.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MenuReturnButton(onNavigateBack)
                        Spacer(Modifier.width(16.dp))
                        Text(
                            "Client Details",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().widthIn(max = 1200.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ClientInfoCard(client)
                        ClientAddressesCard(addresses)
                    }
                }

                item {
                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                }

                item {
                    ExpandableSectionHeader(
                        title = "Orders (${orders.size})",
                        isExpanded = isOrdersExpanded,
                        onToggle = { isOrdersExpanded = !isOrdersExpanded }
                    )
                }
                if (isOrdersExpanded) {
                    if (orders.isEmpty()) {
                        item { Text("No orders found.", modifier = Modifier.padding(16.dp)) }
                    } else {
                        items(orders, key = { it.id }) { order ->
                            Box(modifier = Modifier.widthIn(max = 1200.dp).padding(horizontal = 16.dp)) {
                                ClientOrderItem(order, onOrderClick)
                            }
                        }
                    }
                }

                item {
                    HorizontalDivider()
                }

                item {
                    ExpandableSectionHeader(
                        title = "Reviews (${reviews.size})",
                        isExpanded = isReviewsExpanded,
                        onToggle = { isReviewsExpanded = !isReviewsExpanded }
                    )
                }
                if (isReviewsExpanded) {
                    if (reviews.isEmpty()) {
                        item { Text("No reviews found.", modifier = Modifier.padding(16.dp)) }
                    } else {
                        items(reviews, key = { it.id }) { review ->
                            Box(modifier = Modifier.widthIn(max = 1200.dp).padding(horizontal = 16.dp)) {
                                ClientReviewItem(review, onProductClick)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandableSectionHeader(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 1200.dp)
            .clickable(onClick = onToggle)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onToggle) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.rotate(rotationAngle)
            )
        }
    }
}