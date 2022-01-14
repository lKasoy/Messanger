package com.example.messenger.repository

import com.example.messenger.repository.servermodel.MessageDto
import com.example.messenger.repository.servermodel.User
import kotlinx.coroutines.flow.Flow

interface ServerInterface {

    val isConnected: Flow<Boolean>
    val userList: Flow<List<User>>
    val newMessage: Flow<Any>
}