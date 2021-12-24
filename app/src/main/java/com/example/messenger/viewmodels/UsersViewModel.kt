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

class UsersViewModel(private val decoratorRepository: DecoratorRepository) : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = _loadingState

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    fun fetchUsers(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loadingState.postValue(LoadingState.LOADING)
                _users.postValue(decoratorRepository.getUsers(id))
                _loadingState.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                e.printStackTrace()
                _loadingState.postValue(LoadingState.ERROR)
            }
        }
    }

    fun logOut(id: String, code: Int){
        viewModelScope.launch {
            try {
                decoratorRepository.logOut(id, code)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}