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
import org.alessandrosinibaldi.droidemporium.androidCart.presentation.CheckoutScreen

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

    }
}
