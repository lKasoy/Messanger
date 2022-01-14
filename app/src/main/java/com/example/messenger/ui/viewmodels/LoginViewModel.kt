package com.example.messenger.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.repository.ServerRepository
import com.example.messenger.services.LoadingState
import com.example.messenger.services.SharedPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginViewModel(
    private val serverRepository: ServerRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = _loadingState

    fun login(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                serverRepository.login(userName = userName)
                serverRepository.isConnected.collect {
                    if (it) {
                        _loadingState.postValue(LoadingState.SUCCESS)
                        sharedPrefs.saveUser(userName = userName)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("test", e.toString())
            }
        }
    }

    fun getUserName(): String {
        return sharedPrefs.getUserName()
    }
}



