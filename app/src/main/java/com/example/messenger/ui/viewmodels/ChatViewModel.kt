package com.example.messenger.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.repository.ServerRepository
import com.example.messenger.repository.db.entitydb.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(
    private val serverRepository: ServerRepository,
    private val receiverId: String
) : ViewModel() {

    private val _listMessages = MutableLiveData<List<Message>>()
    val listMessages: MutableLiveData<List<Message>> = _listMessages

    init {
        subscribeNewMessages()
    }

    fun getMessageList() {
        viewModelScope.launch(Dispatchers.IO) {
            val messages = serverRepository.getMessageList()
            _listMessages.postValue(messages)
        }
    }

    private fun subscribeNewMessages() {
        viewModelScope.launch {
            serverRepository.newMessage.collect {
                val currentListMessages = _listMessages.value ?: listOf()
                _listMessages.postValue(currentListMessages + it)
            }
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            try {
                serverRepository.sendMessage(receiverId, message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}