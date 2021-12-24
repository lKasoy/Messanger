package com.example.messenger.di

import com.example.messenger.repository.DecoratorRepository
import com.example.messenger.repository.ServerRepository

object DI {

    val serverRepository: ServerRepository by lazy {
        ServerRepository()
    }

    val decoratorRepository: DecoratorRepository by lazy {
        DecoratorRepository(serverRepository)
    }
}