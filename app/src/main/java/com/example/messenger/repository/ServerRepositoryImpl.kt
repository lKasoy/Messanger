package com.example.messenger.repository

import com.example.messenger.repository.db.MessageDao
import com.example.messenger.repository.db.entitydb.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

class ServerRepositoryImpl(
    private val udpConnection: UdpConnection,
    private val tcpConnection: TcpConnection,
    private val messageDao: MessageDao
) : ServerRepository {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override val userList = tcpConnection.userList.map { it }
    override val isConnected = tcpConnection.isConnected.map { it }
    override val newMessage = MutableSharedFlow<Message>()

    init {
        scope.launch {
            tcpConnection.newMessage.collect {
                val receivedMessage = Message(
                    id = UUID.randomUUID().toString(),
                    senderId = it.from.id,
                    receiverId = tcpConnection.getUserId(),
                    message = it.message
                )
                newMessage.emit(receivedMessage)
                messageDao.addMessage(receivedMessage)
            }
        }
    }

    override fun login(userName: String) {
        scope.launch {
            tcpConnection.startTcpConnection(
                udpConnection.startUdp(), userName
            )
            messageDao.deleteMessages()
        }
    }

    override fun sendGetUsers() {
        tcpConnection.sendGetUsers()
    }

    override fun sendMessage(receiverId: String, message: String) {
        val userMessage = Message(
            id = UUID.randomUUID().toString(),
            senderId = tcpConnection.getUserId(),
            receiverId = receiverId,
            message = message
        )
        tcpConnection.sendMessage(
            userId = tcpConnection.getUserId(),
            receiverId = receiverId,
            message = message
        )
        scope.launch {
            messageDao.addMessage(userMessage)
            newMessage.emit(userMessage)
        }
    }

    override suspend fun getMessageList(): List<Message> {
        return messageDao.getMessagesList()
    }

    override fun sendDisconnect() {
        tcpConnection.sendDisconnect()
    }
}
