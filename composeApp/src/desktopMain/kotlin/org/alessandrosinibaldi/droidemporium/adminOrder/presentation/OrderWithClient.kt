package org.alessandrosinibaldi.droidemporium.adminOrder.presentation

import org.alessandrosinibaldi.droidemporium.adminClient.domain.Client
import org.alessandrosinibaldi.droidemporium.adminOrder.domain.Order

data class OrderWithClient(
    val order: Order,
    val client: Client?
)
