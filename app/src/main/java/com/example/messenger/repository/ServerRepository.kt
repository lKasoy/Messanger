package com.example.messenger.repository

import com.example.messenger.repository.db.Database
import com.example.messenger.repository.db.entitydb.Message
import com.example.messenger.repository.servermodel.MessageDto
import com.example.messenger.repository.servermodel.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class ServerRepository(
    private val udpConnection: UdpConnection,
    private val tcpConnection: TcpConnection,
    private val database: Database
) {
    private val job by lazy { SupervisorJob() }
    private val scope by lazy { CoroutineScope(Dispatchers.IO + job) }

    val userList by lazy {
        MutableSharedFlow<List<User>>()
    }
    val newMessage by lazy {
        MutableSharedFlow<Message>()
    }

    init {
        scope.launch {
            tcpConnection.newMessage.collect {
                val messageDto: MessageDto = it
                val message = Message(
                    id = UUID.randomUUID().toString(),
                    senderId = messageDto.from.id,
                    senderName = messageDto.from.name,
                    receiverId = getCurrentUser().id,
                    receiverName = getCurrentUser().name,
                    message = messageDto.message
                )
                newMessage.emit(message)
                database.addMessage(message)
            }
        }

        scope.launch {
            tcpConnection.users.collect {
                userList.emit(it.users)
            }
        }
    }

    fun startConnection() {
        tcpConnection.startTcpConnection(udpConnection.startUdpConnection())
    }

    fun login(userName: String) {
        tcpConnection.sendConnect(userName)
    }

    fun sendGetUsers() {
        tcpConnection.sendGetUsers()
    }

    fun getCurrentUser(): User {
        return tcpConnection.getCurrentUser()
    }

    fun sendMessage(message: Message) {
        tcpConnection.sendMessage(message)
        scope.launch {
            database.addMessage(message)
        }
    }

    suspend fun getMessageList(): List<Message> {
        return database.getAllMessages()
    }

    fun sendDisconnect() {
        tcpConnection.sendDisconnect()
    }
}
