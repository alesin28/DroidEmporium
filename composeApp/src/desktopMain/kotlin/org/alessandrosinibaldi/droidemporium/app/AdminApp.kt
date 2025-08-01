package org.alessandrosinibaldi.droidemporium.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.alessandrosinibaldi.droidemporium.adminCategory.presentation.CategoryListScreen
import org.alessandrosinibaldi.droidemporium.adminClient.presentation.ClientListScreen
import org.alessandrosinibaldi.droidemporium.adminOrder.presentation.OrderDetailScreen
import org.alessandrosinibaldi.droidemporium.adminOrder.presentation.orderListScreen
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductDetailScreen
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductFormScreen
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductScreen
import org.alessandrosinibaldi.droidemporium.adminClient.presentation.ClientDetailScreen



@Composable
fun AdminApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Login
    ) {

        composable<Route.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Route.StartMenu) {
                        popUpTo(Route.Login) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<Route.StartMenu> {
            StartMenu(navController = navController)
        }

        composable<Route.ProductList> {
            ProductScreen(navController = navController)
        }

        composable<Route.ProductDetail> { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ProductDetailScreen(
                productId = productId,
                navController = navController
            )
        }


        composable<Route.ProductAdd> {
            ProductFormScreen(
                productId = null,
                navController = navController
            )
        }

        composable<Route.ProductEdit> { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")

            ProductFormScreen(
                productId = productId,
                navController = navController
            )

        }

        composable<Route.CategoryList> {
            CategoryListScreen(navController = navController)

        }

        composable<Route.ClientList> {
            ClientListScreen(navController = navController)
        }

        composable<Route.ClientDetail> { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId")
            ClientDetailScreen(
                clientId = clientId,
                navController = navController
            )
        }

        composable<Route.OrderList> {
            orderListScreen(navController = navController)
        }

        composable<Route.OrderDetail> { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            OrderDetailScreen(
                orderId = orderId,
                navController = navController
                )
        }

    }
}