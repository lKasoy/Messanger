package com.example.messenger.di

import androidx.room.Room
import com.example.messenger.repository.ServerRepository
import com.example.messenger.repository.TcpConnection
import com.example.messenger.repository.UdpConnection
import com.example.messenger.repository.db.AppDatabase
import com.example.messenger.services.SharedPrefs
import com.example.messenger.ui.viewmodels.ChatViewModel
import com.example.messenger.ui.viewmodels.LoginViewModel
import com.example.messenger.ui.viewmodels.UsersViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        LoginViewModel(get(), get())
    }
    viewModel {
        UsersViewModel(get(), get())
    }
    viewModel { parameters ->
        ChatViewModel(get(), parameters[0])
    }
}

val udpConnectionModule = module {

    factory { UdpConnection() }
    factory { TcpConnection() }
}

val dataBaseModule = module {

    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "messages").build() }
    single { get<AppDatabase>().getDatabaseDao }
}

val repositoryModule = module {

    single { ServerRepository(get(), get(), get()) }
}

val sharedPreferencesModule = module {

    single { SharedPrefs(androidContext()) }
}


