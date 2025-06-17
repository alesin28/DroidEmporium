package org.alessandrosinibaldi.droidemporium

import android.app.Application
import org.alessandrosinibaldi.droidemporium.di.androidAppModule
import org.koin.core.context.GlobalContext.startKoin

class StartApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(androidAppModule)
        }
    }
}