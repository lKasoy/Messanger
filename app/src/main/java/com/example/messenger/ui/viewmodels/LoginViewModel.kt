package com.example.messenger.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.repository.ServerRepositorySample
import com.example.messenger.services.SharedPrefsSample
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginViewModel(
    private val serverRepositorySample: ServerRepositorySample,
    private val sharedPrefsSample: SharedPrefsSample
) : ViewModel() {

    init {
        val userName = sharedPrefsSample.getUserName()
        if (userName != "")
            login(userName)
    }

    private val _loadingState = MutableLiveData(false)
    val loadingState: LiveData<Boolean> = _loadingState

    fun login(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                serverRepositorySample.login(userName = userName)
                serverRepositorySample.isConnected.collect {
                    if (it) {
                        _loadingState.postValue(true)
                        sharedPrefsSample.saveUser(userName = userName)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("test", e.toString())
            }
        }
    }
}



