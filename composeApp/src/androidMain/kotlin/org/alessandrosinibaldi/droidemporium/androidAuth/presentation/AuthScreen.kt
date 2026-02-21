package org.alessandrosinibaldi.droidemporium.androidAuth.presentation


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.alessandrosinibaldi.droidemporium.app.ClientApp
import org.alessandrosinibaldi.droidemporium.ui.theme.DroidEmporiumTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthScreen() {
    val viewModel: AuthViewModel = koinViewModel()
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    var showLogin by rememberSaveable { mutableStateOf(true) }

    DroidEmporiumTheme {
        when (authState) {
            AuthState.Authenticated -> {
                ClientApp()
            }

            AuthState.Unauthenticated -> {
                if (showLogin) {
                    LoginScreen(
                        viewModel = viewModel,
                        onNavigateToSignup = { showLogin = false }
                    )
                } else {
                    SignupScreen(
                        viewModel = viewModel,
                        onNavigateToLogin = { showLogin = true }
                    )
                }
            }

            AuthState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}