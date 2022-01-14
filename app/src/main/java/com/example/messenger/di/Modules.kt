package com.example.messenger.di

import androidx.room.Room
import com.example.messenger.repository.*
import com.example.messenger.repository.db.AppDatabase
import com.example.messenger.services.SharedPrefsImpl
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

    single<UdpConnection> { UdpConnectionImpl() }
    factory<TcpConnection> { TcpConnectionImpl() }
}

val dataBaseModule = module {

    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "messages").build() }
    single { get<AppDatabase>().getMessageDao }
}

val repositoryModule = module {

    single<ServerRepository> { ServerRepositoryImpl(get(), get(), get()) }
}

val sharedPreferencesModule = module {

    single<SharedPrefs> { SharedPrefsImpl(androidContext()) }
}


