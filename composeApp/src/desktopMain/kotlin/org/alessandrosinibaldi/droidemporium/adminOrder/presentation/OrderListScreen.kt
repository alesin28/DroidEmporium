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
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.Order
import org.koin.compose.viewmodel.koinViewModel
import org.alessandrosinibaldi.droidemporium.adminClient.domain.Client
import org.alessandrosinibaldi.droidemporium.adminOrder.components.OrderItem
import org.alessandrosinibaldi.droidemporium.app.Route


@Composable
fun orderListScreen(
    viewModel: OrderListViewModel = koinViewModel(),
    navController: NavHostController
) {

    val orders by viewModel.orders.collectAsState()

    val onNavigateToOrderDetail: (String) -> Unit = { orderId ->
        navController.navigate(Route.OrderDetail(orderId = orderId))
    }

    orderListScreenContent(
        orders = orders,
        onNavigateToOrderDetail = onNavigateToOrderDetail
    )

}

@Composable
fun orderListScreenContent(
    orders: Pair<List<Order>, List<Client>>,
    onNavigateToOrderDetail: (String) -> Unit = {}
) {

    val orderList = orders.first
    val clientList = orders.second

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
                            items(orderList) { order ->
                                OrderItem(
                                    order = order,
                                    client = clientList.find { it.id == order.clientId },
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