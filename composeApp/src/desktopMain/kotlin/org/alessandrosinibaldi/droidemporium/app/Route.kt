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

    @Serializable
    data object CategoryList : Route()

    @Serializable
    data object StartMenu : Route()

    @Serializable
    data object ClientList : Route()

    @Serializable
    data object OrderList : Route()

}