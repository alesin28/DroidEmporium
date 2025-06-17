package org.alessandrosinibaldi.droidemporium.adminOrder.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order

@Composable
fun OrderItem(
    order: Order,
    client: Client?,
    onNavigateToOrderDetail: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onNavigateToOrderDetail(order.id.toString()) }
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "ID: ${order.id}"
            )
            Text(
                text = "Client: ${client?.displayName}"
            )
            Text(
                text = "Total: ${order.totalAmount}"
            )
        }
    }
}