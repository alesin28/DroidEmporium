package org.alessandrosinibaldi.droidemporium.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.alessandrosinibaldi.droidemporium.ui.theme.DroidEmporiumTheme

@Composable
fun App() {
    DroidEmporiumTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Route.StartMenu.path
        ) {
            composable(Route.StartMenu.path) {
                StartMenu(navController = navController)
            }
        }
    }
}