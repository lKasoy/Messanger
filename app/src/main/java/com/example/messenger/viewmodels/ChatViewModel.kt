package com.example.messenger.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.repository.DecoratorRepository
import com.example.messenger.repository.ServerRepository
import kotlinx.coroutines.launch

class ChatViewModel(private val decoratorRepository: DecoratorRepository): ViewModel() {

    private val _messageList = MutableLiveData<String>()
    val messageList: LiveData<String> = _messageList

//    init {
//        fetchMessageList()
//    }

    fun fetchMessageList() {
        viewModelScope.launch {
            try {



            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(id: String, receiver: String, message: String){
        viewModelScope.launch {
            try {
                decoratorRepository.sendMessage(id, receiver, message)
                _messageList.postValue(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

}