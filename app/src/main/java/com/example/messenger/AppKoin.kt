package com.example.messenger

import android.app.Application
import com.example.messenger.di.dataBaseModule
import com.example.messenger.di.repositoryModule
import com.example.messenger.di.udpConnectionModule
import com.example.messenger.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppKoin: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AppKoin)
            modules(listOf(udpConnectionModule, dataBaseModule, repositoryModule, viewModelModule))
        }
    }
}