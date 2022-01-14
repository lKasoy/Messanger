package com.example.messenger.repository

import com.example.messenger.repository.db.DatabaseDao
import com.example.messenger.repository.db.entitydb.Message
import com.example.messenger.repository.servermodel.MessageDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

class ServerRepository(
    private val udpConnection: UdpConnection,
    private val tcpConnection: TcpConnection,
    private val databaseDao: DatabaseDao
) : ServerInterface {

    private val job by lazy { SupervisorJob() }
    private val scope by lazy { CoroutineScope(Dispatchers.IO + job) }
    private val userID by lazy { tcpConnection.getUserId() }

    override val userList = tcpConnection.userList.map { it }
    override val isConnected = tcpConnection.isConnected.map { it }
    override val newMessage = MutableSharedFlow<Message>()

    init {
        scope.launch {
            tcpConnection.newMessage.collect {
                val receivedMessage = Message(
                    id = UUID.randomUUID().toString(),
                    senderId = it.from.id,
                    receiverId = userID,
                    message = it.message
                )
                newMessage.emit(receivedMessage)
                databaseDao.addMessage(receivedMessage)
            }
        }
    }

    fun login(userName: String) {
        scope.launch {
            tcpConnection.startTcpConnection(
                udpConnection.startUdpConnection(), userName
            )
            databaseDao.deleteMessages()
        }
    }

    fun sendGetUsers() {
        tcpConnection.sendGetUsers()
    }

    fun sendMessage(receiverId: String, message: String) {
        val userMessage = Message(
            id = UUID.randomUUID().toString(),
            senderId = userID,
            receiverId = receiverId,
            message = message
        )
        tcpConnection.sendMessage(
            userId = userID,
            receiverId = receiverId,
            message = message
        )
        scope.launch {
            databaseDao.addMessage(userMessage)
            newMessage.emit(userMessage)
        }
    }

    suspend fun getMessageList(): List<Message> {
        return databaseDao.getMessagesList()
    }

    fun sendDisconnect() {
        tcpConnection.sendDisconnect()
    }
}
