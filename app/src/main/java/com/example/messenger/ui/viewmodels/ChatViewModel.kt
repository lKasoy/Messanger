package com.example.messenger.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.repository.db.entitydb.Message
import com.example.messenger.di.DI
import com.example.messenger.repository.ServerRepository
import com.example.messenger.repository.servermodel.BaseDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(private val serverRepository: ServerRepository) : ViewModel() {

    private val _messageList = MutableLiveData<List<Message>>()
    val messageList: MutableLiveData<List<Message>> = _messageList

    init {
        refreshMessagesList()
        subscribeNewMessages()
    }

    private fun subscribeNewMessages() {
        viewModelScope.launch {
            DI.messageFromServerFlow.collect { baseDto ->
                if (baseDto.action == BaseDto.Action.NEW_MESSAGE) {
                    delay(1000)
                    _messageList.postValue(serverRepository.getMessages())
                    Log.d("test", "refresh message list")
                }
            }
        }
    }

    fun refreshMessagesList() {
        viewModelScope.launch {
            _messageList.postValue(serverRepository.getMessages())
        }
    }

    fun sendMessage(id: String, receiver: String, message: String) {
        viewModelScope.launch {
            try {
                serverRepository.sendMessage(id, receiver, message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}