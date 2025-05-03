package org.alessandrosinibaldi.droidemporium.app

import kotlinx.serialization.Serializable

interface Route {

    @Serializable
    data object ProductList: Route

    @Serializable
    data class ProductAdd(val productId: String? = null): Route
}