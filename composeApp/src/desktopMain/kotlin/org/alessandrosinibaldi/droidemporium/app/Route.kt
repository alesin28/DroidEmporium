package org.alessandrosinibaldi.droidemporium.app

import kotlinx.serialization.Serializable

interface Route {

    @Serializable
    data object ProductList: Route

    @Serializable
    data object ProductAdd: Route
}