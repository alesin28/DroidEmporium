package org.alessandrosinibaldi.droidemporium.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductDetailScreen
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductFormScreen
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.ProductScreen


@Composable
fun AdminApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.ProductList
    ) {
        composable<Route.ProductList> {
            ProductScreen(navController = navController)
        }

        composable<Route.ProductDetail> { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ProductDetailScreen(productId = productId)
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
    }
}