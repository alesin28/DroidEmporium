package org.alessandrosinibaldi.droidemporium.adminOrder.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
// We no longer need the separate Order and Client domain imports here
// import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order
// import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client
import org.koin.compose.viewmodel.koinViewModel
import org.alessandrosinibaldi.droidemporium.adminOrder.components.OrderItem
import org.alessandrosinibaldi.droidemporium.app.Route

@Composable
fun orderListScreen(
    viewModel: OrderListViewModel = koinViewModel(),
    navController: NavHostController
) {
    val ordersWithClients by viewModel.ordersWithClients.collectAsState()

    val onNavigateToOrderDetail: (String) -> Unit = { orderId ->
        navController.navigate(Route.OrderDetail(orderId = orderId))
    }

    orderListScreenContent(
        ordersWithClients = ordersWithClients,
        onNavigateToOrderDetail = onNavigateToOrderDetail
    )
}

@Composable
fun orderListScreenContent(
    ordersWithClients: List<OrderWithClient>,
    onNavigateToOrderDetail: (String) -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(3f)
                ) {
                    Row(
                        modifier = Modifier.weight(4f).background(color = Color.LightGray)
                    ) {
                        LazyColumn {
                            items(ordersWithClients) { orderWithClient ->
                                OrderItem(
                                    order = orderWithClient.order,
                                    client = orderWithClient.client,
                                    onNavigateToOrderDetail = onNavigateToOrderDetail
                                )
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f)
                ) {
                    Text("Filters")
                }
            }
        }
    }
}