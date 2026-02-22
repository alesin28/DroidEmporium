package org.alessandrosinibaldi.droidemporium.app

import kotlinx.serialization.Serializable


@Serializable
sealed class Route(val path: String) {
    @Serializable
    data object StartMenu : Route("start_menu")

    @Serializable
    data class ProductList(
        val categoryId: String? = null,
        val categoryName: String? = null,
        val showNewest: Boolean = false,
        val startSearch: Boolean = false,
        val query: String? = null

    ) : Route("product_list")

    @Serializable
    data class ProductDetail(
        val productId: String
    ) : Route("product_detail/{productId}")

    @Serializable
    data object Cart : Route("cart")

}