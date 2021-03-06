package com.example.messenger.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.repository.ServerRepository
import com.example.messenger.repository.servermodel.User
import com.example.messenger.services.SharedPrefs
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UsersViewModel(
    private val serverRepository: ServerRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    init {
        subscribeUsers()
    }

    private fun subscribeUsers() {
        viewModelScope.launch {
            serverRepository.userList.collect {
                _users.postValue(it)
            }
        }
    }

    fun sendGetUsers() {
        viewModelScope.launch {
            serverRepository.sendGetUsers()
        }
    }

    fun logOut() {
        viewModelScope.launch {
            try {
                serverRepository.sendDisconnect()
                sharedPrefs.resetUserName()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
