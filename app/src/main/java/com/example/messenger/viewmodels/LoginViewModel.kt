package com.example.messenger.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.repository.DecoratorRepository
import com.example.messenger.services.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(private val decoratorRepository: DecoratorRepository) : ViewModel() {

    private var ip: String = ""

    private val _loadingTCPIIP = MutableLiveData<LoadingState>()
    val loadingTCPIP: LiveData<LoadingState> = _loadingTCPIIP

    private val _loadingUdp = MutableLiveData<LoadingState>()
    val loadingUdp: LiveData<LoadingState> = _loadingUdp

    fun startConnection() {
        viewModelScope.launch(Dispatchers.IO) {
            while (ip == "") {
                try {
                    _loadingUdp.postValue(LoadingState.LOADING)
                    ip = decoratorRepository.startUdp()
                    _loadingUdp.postValue(LoadingState.SUCCESS)
                } catch (e: Exception) {
                    e.printStackTrace()
                    _loadingUdp.postValue(LoadingState.ERROR)
                }
            }
        }
    }

    fun login(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loadingTCPIIP.postValue(LoadingState.LOADING)
                decoratorRepository.startTCPIP(ip)
                val id = decoratorRepository.getId()
                decoratorRepository.connect(id, userName)
                _loadingTCPIIP.postValue(LoadingState.SUCCESS)
            } catch (e: Exception) {
                e.printStackTrace()
                _loadingTCPIIP.value = LoadingState.ERROR
            }
        }
    }

    fun getId(): String {
        return try {
            decoratorRepository.getId()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun sendPing(id: String) {
        var isConnect = true
        viewModelScope.launch(Dispatchers.IO) {
            while (isConnect){
                try {
                    isConnect = decoratorRepository.sendPing(id)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("test", "Disconnected from the server")
                }
                delay(8000)
            }
        }
    }
}