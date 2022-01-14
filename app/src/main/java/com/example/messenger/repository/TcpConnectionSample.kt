package com.example.messenger.repository

import com.example.messenger.repository.servermodel.BaseDto
import com.example.messenger.repository.servermodel.MessageDto
import com.example.messenger.repository.servermodel.Payload
import com.example.messenger.repository.servermodel.User
import kotlinx.coroutines.flow.Flow

interface TcpConnectionSample {

    val isConnected: Flow<Boolean>
    val userList: Flow<List<User>>
    val newMessage: Flow<MessageDto>

    fun startTcpConnection(ip: String, userName: String)

    fun sendConnect()

    fun sendPing()

    fun sendGetUsers()

    fun sendMessage(userId: String, receiverId: String, message: String)

    fun receiveAnswer()

    fun sendToServer(action: BaseDto.Action, payload: Payload): String

    fun jsonToPayload(response: String): BaseDto

    fun sendDisconnect()

    fun getUserId(): String
}