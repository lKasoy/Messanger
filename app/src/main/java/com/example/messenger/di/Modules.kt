package com.example.messenger.di

import androidx.room.Room
import com.example.messenger.repository.db.AppDatabase
import com.example.messenger.repository.db.DatabaseDao
import com.example.messenger.repository.ServerRepository
import com.example.messenger.repository.TcpConnection
import com.example.messenger.repository.UdpConnection
import com.example.messenger.ui.viewmodels.ChatViewModel
import com.example.messenger.ui.viewmodels.LoginViewModel
import com.example.messenger.ui.viewmodels.UsersViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        LoginViewModel(get())
    }
    viewModel {
        UsersViewModel(get())
    }
    viewModel {
        ChatViewModel(get())
    }
}

val udpConnectionModule = module {
    factory { UdpConnection() }
    factory { TcpConnection() }
}

val dataBaseModule = module {

    fun provideDao(appDatabase: AppDatabase): DatabaseDao {
        return appDatabase.getDatabaseDao
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "db")
            .build()
    }
    single { provideDao( get()) }
}

val repositoryModule = module {

    fun provideServerRepository(
        udpConnection: UdpConnection,
        tcpConnection: TcpConnection,
        databaseDao: DatabaseDao
    ): ServerRepository {
        return ServerRepository(udpConnection, tcpConnection, databaseDao)
    }

    single { provideServerRepository(get(), get(), get()) }


}
