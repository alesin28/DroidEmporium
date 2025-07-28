package org.alessandrosinibaldi.droidemporium.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.alessandrosinibaldi.droidemporium.home.presentation.HomeScreen

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


    }
}
