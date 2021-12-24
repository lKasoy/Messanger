package com.example.messenger.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.messenger.repository.DecoratorRepository
import com.example.messenger.repository.ServerRepository
import com.example.messenger.viewmodels.ChatViewModel
import com.example.messenger.viewmodels.LoginViewModel
import com.example.messenger.viewmodels.UsersViewModel

class LoginViewModelFactory(private val decoratorRepository: DecoratorRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(decoratorRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}

class UsersListViewModelFactory(private val decoratorRepository: DecoratorRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            return UsersViewModel(decoratorRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}

class ChatViewModelFactory(private val decoratorRepository: DecoratorRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(decoratorRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}