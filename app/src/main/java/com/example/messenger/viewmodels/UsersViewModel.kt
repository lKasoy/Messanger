package com.example.messenger.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.model.User
import com.example.messenger.repository.DecoratorRepository
import com.example.messenger.repository.ServerRepository
import com.example.messenger.services.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsersViewModel(
    private val decoratorRepository: DecoratorRepository
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    fun fetchUsers(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentUsers = _users.value ?: listOf()
                _users.postValue(currentUsers + decoratorRepository.getUsers(id))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun logOut(id: String, code: Int) {
        viewModelScope.launch {
            try {
                decoratorRepository.logOut(id, code)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}