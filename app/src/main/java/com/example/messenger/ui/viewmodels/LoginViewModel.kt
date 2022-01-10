package com.example.messenger.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.repository.ServerRepository
import com.example.messenger.services.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(private val serverRepository: ServerRepository) : ViewModel() {

    private var ip: String = ""

    private val _udpConnection = MutableLiveData<LoadingState>()
    val udpConnection: LiveData<LoadingState> = _udpConnection

    fun startConnection() {
        viewModelScope.launch(Dispatchers.IO) {
            while (ip == "") {
                try {
                    _udpConnection.postValue(LoadingState.LOADING)
                    serverRepository.clearDb()
                    ip = serverRepository.startUdpConnection()
                    _udpConnection.postValue(LoadingState.SUCCESS)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun startTCPIP() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                serverRepository.isConnected.postValue(true)
                serverRepository.startConnection(ip)
                serverRepository.receiveAnswer()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun login(id: String, userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                serverRepository.sendConnect(id, userName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendPing(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                while (serverRepository.isConnected.value == true) {
                    serverRepository.sendPing(id)
                    delay(9000)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


