package com.example.messenger.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.repository.ServerRepository
import com.example.messenger.repository.db.entitydb.Message
import com.example.messenger.repository.db.entitydb.User
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(private val serverRepository: ServerRepository) : ViewModel() {

    private val _newMessage = MutableLiveData<Message>()
    val newMessage: MutableLiveData<Message> = _newMessage

    init {
        subscribeNewMessages()
    }

    private fun subscribeNewMessages() {
        viewModelScope.launch {
            serverRepository.newMessage.collect {
                _newMessage.postValue(it)
            }
        }
    }


    fun sendMessage(receiver: String, message: String) {
        viewModelScope.launch {
            try {
                serverRepository.sendMessage(receiver, message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getCurrentUser(): User{
        return serverRepository.getCurrentUser()
    }
}