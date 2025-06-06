package org.alessandrosinibaldi.droidemporium

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import android.app.Application
import dev.gitlive.firebase.app
import org.alessandrosinibaldi.droidemporium.app.AdminApp
import org.alessandrosinibaldi.droidemporium.di.appModule
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

    try {
        FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
            val storage = mutableMapOf<String, String>()
            override fun clear(key: String) {
                storage.remove(key)
            }

            override fun log(msg: String) = println(msg)
            override fun retrieve(key: String) = storage[key]
            override fun store(key: String, value: String) = storage.set(key, value)
        })

        val options = FirebaseOptions(
            projectId = projectId,
            applicationId = appId,
            apiKey = apiKey
        )

        Firebase.initialize(Application(), options)
        val app = Firebase.app
        println("Initialized Project ID: ${app.options.projectId}")


    } catch (e: Exception) {
        e.printStackTrace()
        return
    }

    startKoin {
        modules(appModule)
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "DroidEmporium",
        ) {
            AdminApp()
        }
    }
}
