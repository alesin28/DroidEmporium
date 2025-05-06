package org.alessandrosinibaldi.droidemporium.app

import kotlinx.serialization.Serializable

@Serializable
sealed class Route {

    @Serializable
    data object ProductList : Route()

    @Serializable
    data object ProductAdd : Route()

    @Serializable
    data class ProductDetail(val productId: String) : Route()

    @Serializable
    data class ProductEdit(val productId: String) : Route()
}