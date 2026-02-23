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

    @Serializable
    data object Checkout : Route("checkout")

    @Serializable
    data class AddressForm(
        val addressId: String? = null
    ) : Route("address_form")

    @Serializable
    data object Profile : Route("profile")

    @Serializable
    data object OrderHistory : Route("order_history")

    @Serializable
    data object AddressList : Route("address_list")

    @Serializable
    data class OrderDetail(val orderId: String) : Route("order_detail/{orderId}")

    @Serializable
    data class AddReview(val productId: String, val productName: String) :
        Route("add_review/{productId}")
}