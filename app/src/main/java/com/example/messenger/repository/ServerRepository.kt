package com.example.messenger.repository

import com.example.messenger.repository.db.DatabaseDao
import com.example.messenger.repository.db.entitydb.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

class ServerRepository(
    private val udpConnectionSample: UdpConnectionSample,
    private val tcpConnectionSample: TcpConnectionSample,
    private val databaseDao: DatabaseDao
) : ServerRepositorySample {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override val userList = tcpConnectionSample.userList.map { it }
    override val isConnected = tcpConnectionSample.isConnected.map { it }
    override val newMessage = MutableSharedFlow<Message>()

    init {
        scope.launch {
            tcpConnectionSample.newMessage.collect {
                val receivedMessage = Message(
                    id = UUID.randomUUID().toString(),
                    senderId = it.from.id,
                    receiverId = tcpConnectionSample.getUserId(),
                    message = it.message
                )
                newMessage.emit(receivedMessage)
                databaseDao.addMessage(receivedMessage)
            }
        }
    }

    override fun login(userName: String) {
        scope.launch {
            tcpConnectionSample.startTcpConnection(
                udpConnectionSample.startUdp(), userName
            )
            databaseDao.deleteMessages()
        }
    }

    override fun sendGetUsers() {
        tcpConnectionSample.sendGetUsers()
    }

    override fun sendMessage(receiverId: String, message: String) {
        val userMessage = Message(
            id = UUID.randomUUID().toString(),
            senderId = tcpConnectionSample.getUserId(),
            receiverId = receiverId,
            message = message
        )
        tcpConnectionSample.sendMessage(
            userId = tcpConnectionSample.getUserId(),
            receiverId = receiverId,
            message = message
        )
        scope.launch {
            databaseDao.addMessage(userMessage)
            newMessage.emit(userMessage)
        }
    }

    override suspend fun getMessageList(): List<Message> {
        return databaseDao.getMessagesList()
    }

    override fun sendDisconnect() {
        tcpConnectionSample.sendDisconnect()
    }
}
