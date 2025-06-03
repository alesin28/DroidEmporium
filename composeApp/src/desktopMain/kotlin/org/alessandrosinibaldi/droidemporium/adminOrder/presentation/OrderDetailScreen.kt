package org.alessandrosinibaldi.droidemporium.adminOrder.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Surface
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.alessandrosinibaldi.droidemporium.adminClient.domain.Client
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.Order
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun OrderDetailScreen(
    orderId: String?,
) {
    val viewModel: OrderDetailViewModel = koinViewModel(
        parameters = { parametersOf(orderId) }
    )

    val order by viewModel.order.collectAsState()
    val client by viewModel.client.collectAsState()

    OrderDetailScreenContent(
        order = order,
        client = client
    )
}

@Composable
fun OrderDetailScreenContent(
    order: Order?,
    client: Client?
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().widthIn(1200.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,

            ) {
                Text("ID: ${order?.id}")
                Text("Client: ${client?.displayName}")
                Text("Order Date: ${order?.orderDate}")
                Text("Lines")
                HorizontalDivider()
                Column(
                    modifier = Modifier.fillMaxSize().widthIn(1200.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,

                ) {
                    order?.lines?.forEach { line ->

                        Row(
                            modifier = Modifier
                                .width(IntrinsicSize.Min).height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.width(IntrinsicSize.Min),
                                text = "Product: ${line.productName}"
                            )
                            VerticalDivider(
                                modifier = Modifier
                                    .height(IntrinsicSize.Min)
                            )
                            Text(
                                modifier = Modifier.width(IntrinsicSize.Min),
                                text = "Quantity: ${line.quantity}"
                            )
                            VerticalDivider(
                                modifier = Modifier
                                    .height(IntrinsicSize.Min)
                            )
                            Text(
                                modifier = Modifier.width(IntrinsicSize.Min),
                                text = "Price: ${line.priceAtPurchase} €"
                            )
                        }
                        HorizontalDivider()

                    }
                    HorizontalDivider()

                    Text("Total Amount: ${order?.totalAmount}")

                }
            }
        }
    }
}

