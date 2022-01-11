package com.example.messenger.repository

import com.example.messenger.repository.db.DatabaseDao
import com.example.messenger.repository.db.entitydb.Message
import com.example.messenger.repository.db.entitydb.User
import com.example.messenger.repository.servermodel.MessageDto
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import java.util.*

class ServerRepository(
    private val udpConnection: UdpConnection,
    private val tcpConnection: TcpConnection,
    private val databaseDao: DatabaseDao
) {
    private var ip: String? = null
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
            tcpConnection.isConnection.collect {
                while (it) {
                    delay(9000)
                    tcpConnection.sendPing()
                }
            }
        }

        scope.launch {
            tcpConnection.newMessage.collect {
                val messageDto: MessageDto = it
                val message = Message(
                    id = UUID.randomUUID().toString(),
                    senderId = messageDto.from.id ,
                    senderName = messageDto.from.name,
                    receiverId = getCurrentUser().id,
                    receiverName = getCurrentUser().name,
                    message = messageDto.message
                )
                newMessage.emit(message)
            }
        }

        scope.launch {
            tcpConnection.users.collect {
                userList.emit(it.users)
            }
        }
    }

    fun startUdpConnection() {
        ip = udpConnection.startUdpConnection()
    }

    fun startTcpConnection() {
        tcpConnection.startTcpConnection(ip!!)
    }

    fun login(userName: String) {
        tcpConnection.sendConnect(userName)
        tcpConnection.sendPing()
    }

    fun sendGetUsers() {
        scope.launch {
            tcpConnection.sendGetUsers()
        }
    }

    suspend fun getUsersFromDb(): List<User> {
        return databaseDao.getUsers()
    }


    fun getCurrentUser(): User {
        return tcpConnection.getCurrentUser()
    }

    suspend fun sendMessage(receiver: String, message: String) {
        tcpConnection.sendMessage(receiver, message)
//        databaseDao.addMessage(Message(UUID.randomUUID().toString(), receiver, message))
    }

    fun sendDisconnect() {
        tcpConnection.sendDisconnect()
        scope.launch {
            tcpConnection.isConnection.emit(false)
        }
    }
    // переделать livedata на флоу
    // избавиться от глобал скоупа
}
