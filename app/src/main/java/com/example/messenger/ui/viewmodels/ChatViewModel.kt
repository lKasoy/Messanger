package com.example.messenger.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.repository.ServerRepositorySample
import com.example.messenger.repository.db.entitydb.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(
    private val serverRepositorySample: ServerRepositorySample,
    private val receiverId: String
) : ViewModel() {

    private val _listMessages = MutableLiveData<List<Message>>()
    val listMessages: MutableLiveData<List<Message>> = _listMessages

    init {
        subscribeNewMessages()
    }

    fun getMessageList() {
        viewModelScope.launch(Dispatchers.IO) {
            val messages = serverRepositorySample.getMessageList()
            _listMessages.postValue(messages)
        }
    }

    private fun subscribeNewMessages() {
        viewModelScope.launch {
            serverRepositorySample.newMessage.collect {
                val currentListMessages = _listMessages.value ?: listOf()
                _listMessages.postValue(currentListMessages + it)
            }
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            try {
                serverRepositorySample.sendMessage(receiverId, message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}