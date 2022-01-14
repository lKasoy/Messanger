package com.example.messenger.repository

import com.example.messenger.repository.db.entitydb.Message
import com.example.messenger.repository.servermodel.User
import kotlinx.coroutines.flow.Flow

interface ServerRepository {

    val isConnected: Flow<Boolean>
    val userList: Flow<List<User>>
    val newMessage: Flow<Message>

    fun login(userName: String)

    fun sendGetUsers()

    fun sendMessage(receiverId: String, message: String)

    suspend fun getMessageList(): List<Message>

    fun sendDisconnect()
}