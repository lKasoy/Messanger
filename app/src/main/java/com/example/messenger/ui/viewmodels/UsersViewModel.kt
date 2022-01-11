package com.example.messenger.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.repository.db.entitydb.User
import com.example.messenger.repository.ServerRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect

class UsersViewModel(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    init {
        subscribeUsers()
    }

    fun sendGetUsers() {
        viewModelScope.launch {
            serverRepository.sendGetUsers()
        }
    }

    private fun subscribeUsers() {
        viewModelScope.launch {
            serverRepository.userList.collect {
                _users.postValue(it)
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            try {
                serverRepository.sendDisconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
