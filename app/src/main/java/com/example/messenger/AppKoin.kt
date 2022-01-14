package com.example.messenger

import android.app.Application
import com.example.messenger.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppKoin : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AppKoin)
            modules(
                listOf(
                    udpConnectionModule,
                    dataBaseModule,
                    repositoryModule,
                    viewModelModule,
                    sharedPreferencesModule
                )
            )
        }
    }
}