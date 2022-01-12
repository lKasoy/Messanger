package com.example.messenger.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.repository.ServerRepository
import com.example.messenger.repository.db.entitydb.Message
import com.example.messenger.repository.servermodel.User
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(private val serverRepository: ServerRepository) : ViewModel() {

    private val _listMessages = MutableLiveData<List<Message>>()
    val listMessages: MutableLiveData<List<Message>> = _listMessages

    private val _newMessage = MutableLiveData<Message>()
    val newMessage: MutableLiveData<Message> = _newMessage

    init {
        subscribeNewMessages()
    }

    fun getMessageList(){
        viewModelScope.launch {
            val messages = serverRepository.getMessageList()
            _listMessages.postValue(messages)
        }
    }

    private fun subscribeNewMessages() {
        viewModelScope.launch {
            serverRepository.newMessage.collect {
                _newMessage.postValue(it)
            }
        }
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            try {
                serverRepository.sendMessage(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getCurrentUser(): User {
        return serverRepository.getCurrentUser()
    }
}