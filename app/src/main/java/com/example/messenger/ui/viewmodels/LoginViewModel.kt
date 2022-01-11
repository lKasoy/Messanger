package com.example.messenger.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.repository.ServerRepository
import com.example.messenger.services.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val serverRepository: ServerRepository) : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = _loadingState

    fun login(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                serverRepository.clearDb()
                serverRepository.startUdpConnection()
                _loadingState.postValue(LoadingState.STARTUDP)
                serverRepository.startTcpConnection()
                _loadingState.postValue(LoadingState.STARTTCP)
                serverRepository.login(userName)
                _loadingState.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


