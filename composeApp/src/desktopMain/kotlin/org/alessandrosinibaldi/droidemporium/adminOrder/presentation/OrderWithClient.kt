package org.alessandrosinibaldi.droidemporium.adminOrder.presentation

import org.alessandrosinibaldi.droidemporium.commonClient.domain.Client
import org.alessandrosinibaldi.droidemporium.commonOrder.domain.Order

data class OrderWithClient(
    val order: Order,
    val client: Client?
)
