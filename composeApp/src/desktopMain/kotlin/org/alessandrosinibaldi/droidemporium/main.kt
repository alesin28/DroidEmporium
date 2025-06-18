package org.alessandrosinibaldi.droidemporium

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import dev.gitlive.firebase.auth.auth
import android.app.Application
import dev.gitlive.firebase.app
import kotlinx.coroutines.runBlocking
import org.alessandrosinibaldi.droidemporium.app.AdminApp
import org.alessandrosinibaldi.droidemporium.di.desktopAppModule
import org.alessandrosinibaldi.droidemporium.ui.theme.DroidEmporiumTheme
import org.koin.core.context.startKoin
import java.io.File
import java.io.FileInputStream
import java.util.Properties

fun main() {



    val properties = Properties()
    val configFile = File("firebase_config.properties")
    FileInputStream(configFile).use { input ->
            properties.load(input)
        }
    var apiKey: String = properties.getProperty("apiKey")
    var appId: String = properties.getProperty("applicationId")
    var projectId: String? = properties.getProperty("projectId")
    var adminEmail: String = properties.getProperty("adminEmail")
    var adminPassword: String = properties.getProperty("adminPassword")

    runBlocking {
        try {
            FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
                val storage = mutableMapOf<String, String>()
                override fun clear(key: String) { storage.remove(key) }
                override fun log(msg: String) = println(msg)
                override fun retrieve(key: String): String? = storage[key]
                override fun store(key: String, value: String) { storage[key] = value }
            })

            val options = FirebaseOptions(
                projectId = projectId,
                applicationId = appId,
                apiKey = apiKey
            )

            Firebase.initialize(Application(), options)
            println("Initialized Project ID: ${Firebase.app.options.projectId}")
            Firebase.auth.signInWithEmailAndPassword(adminEmail, adminPassword)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    startKoin {
        modules(desktopAppModule)
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "DroidEmporium",
        ) {
            DroidEmporiumTheme {
                AdminApp()
            }
        }
    }
}
