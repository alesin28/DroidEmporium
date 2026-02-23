package org.alessandrosinibaldi.droidemporium.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.alessandrosinibaldi.droidemporium.androidProduct.presentation.ProductDetailScreen
import org.alessandrosinibaldi.droidemporium.home.presentation.HomeScreen
import org.alessandrosinibaldi.droidemporium.androidProduct.presentation.ProductListScreen
import org.alessandrosinibaldi.droidemporium.androidCart.presentation.CartScreen
import org.alessandrosinibaldi.droidemporium.androidAddress.presentation.AddressFormScreen
import org.alessandrosinibaldi.droidemporium.androidAddress.presentation.AddressListScreen
import org.alessandrosinibaldi.droidemporium.androidCart.presentation.CheckoutScreen
import org.alessandrosinibaldi.droidemporium.androidClient.presentation.ProfileScreen
import org.alessandrosinibaldi.droidemporium.androidOrder.presentation.ClientOrderDetailScreen
import org.alessandrosinibaldi.droidemporium.androidOrder.presentation.OrderHistoryScreen
import org.alessandrosinibaldi.droidemporium.androidReview.presentation.ReviewFormScreen

@Composable
fun ClientApp() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.StartMenu
    ) {
        composable<Route.StartMenu> {
            HomeScreen(navController = navController)
        }

        composable<Route.ProductList> { backStackEntry ->
            val args = backStackEntry.toRoute<Route.ProductList>()

            ProductListScreen(
                navController = navController,
                routeArgs = args
            )
        }

        composable<Route.ProductDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<Route.ProductDetail>()

            ProductDetailScreen(
                productId = args.productId,
                navController = navController
            )
        }

        composable<Route.Cart> {
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCheckout = {
                    navController.navigate(Route.Checkout)
                })
        }

        composable<Route.Checkout> {
            CheckoutScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddAddress = { navController.navigate(Route.AddressForm(null)) },
                onNavigateToHome = {
                    navController.navigate(Route.StartMenu) {
                        popUpTo(Route.StartMenu) { inclusive = false }
                    }
                }

            )
        }

        composable<Route.AddressForm> { backStackEntry ->
            val args = backStackEntry.toRoute<Route.AddressForm>()
            AddressFormScreen(
                addressId = args.addressId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Route.Profile> {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOrders = { navController.navigate(Route.OrderHistory) },
                onNavigateToAddresses = { navController.navigate(Route.AddressList) }
            )
        }

        composable<Route.OrderHistory> {
            OrderHistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOrderDetail = { orderId ->
                    navController.navigate(Route.OrderDetail(orderId))
                }
            )
        }

        composable<Route.AddressList> {
            AddressListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddressForm = { addressId ->
                    navController.navigate(Route.AddressForm(addressId))
                }
            )
        }

        composable<Route.OrderDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<Route.OrderDetail>()
            ClientOrderDetailScreen(
                orderId = args.orderId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProduct = { productId ->
                    navController.navigate(Route.ProductDetail(productId))
                },
                onNavigateToReviewForm = { productId, productName ->
                    navController.navigate(Route.AddReview(productId, productName))
                }
            )
        }

        composable<Route.AddReview> { backStackEntry ->
            val args = backStackEntry.toRoute<Route.AddReview>()
            ReviewFormScreen(
                productId = args.productId,
                productName = args.productName,
                onNavigateBack = { navController.popBackStack() }
            )
        }

    }
}
