package com.example.messenger.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.repository.ServerRepositorySample
import com.example.messenger.repository.servermodel.User
import com.example.messenger.services.SharedPrefsSample
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UsersViewModel(
    private val serverRepositorySample: ServerRepositorySample,
    private val sharedPrefsSample: SharedPrefsSample
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    init {
        subscribeUsers()
    }

    private fun subscribeUsers() {
        viewModelScope.launch {
            serverRepositorySample.userList.collect {
                _users.postValue(it)
            }
        }
    }

    fun sendGetUsers() {
        viewModelScope.launch {
            serverRepositorySample.sendGetUsers()
        }
    }

    fun logOut() {
        viewModelScope.launch {
            try {
                serverRepositorySample.sendDisconnect()
                sharedPrefsSample.resetUserName()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
