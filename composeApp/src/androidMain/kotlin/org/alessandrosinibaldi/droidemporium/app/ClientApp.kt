package org.alessandrosinibaldi.droidemporium.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.alessandrosinibaldi.droidemporium.home.presentation.HomeScreen
import org.alessandrosinibaldi.droidemporium.androidProduct.presentation.ProductListScreen

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
    }

}
