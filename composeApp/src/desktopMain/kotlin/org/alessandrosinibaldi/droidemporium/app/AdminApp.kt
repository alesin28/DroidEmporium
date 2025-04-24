package org.alessandrosinibaldi.droidemporium.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.alessandrosinibaldi.droidemporium.adminProduct.presentation.AddProductScreen
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

        composable<Route.ProductAdd> {
            AddProductScreen(navController = navController)
        }
    }

}
