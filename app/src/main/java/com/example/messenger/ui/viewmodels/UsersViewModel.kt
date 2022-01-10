package com.example.messenger.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.repository.db.entitydb.User
import com.example.messenger.repository.ServerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UsersViewModel(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    fun fetchUsers(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                serverRepository.sendGetUsers(id)
                delay(1000)
                _users.postValue(serverRepository.getUsers())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun logOut(id: String, code: Int) {
        viewModelScope.launch {
            try {
                serverRepository.sendDisconnect(id, code)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}