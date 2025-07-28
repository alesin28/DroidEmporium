package org.alessandrosinibaldi.droidemporium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.FirebaseApp
import org.alessandrosinibaldi.droidemporium.app.ClientApp
import org.alessandrosinibaldi.droidemporium.ui.theme.DroidEmporiumTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            DroidEmporiumTheme {
                ClientApp()
            }
        }
    }
}

