package com.example.messenger.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.messenger.repository.db.DatabaseDao
import com.example.messenger.repository.db.entitydb.Message
import com.example.messenger.repository.db.entitydb.User
import com.example.messenger.di.DI
import com.example.messenger.repository.servermodel.BaseDto
import com.example.messenger.repository.servermodel.ConnectedDto
import com.example.messenger.repository.servermodel.MessageDto
import com.example.messenger.repository.servermodel.UsersReceivedDto
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class ServerRepository(
    private val udpConnection: UdpConnection,
    private val tcpConnection: TcpConnection,
    private val databaseDao: DatabaseDao
) {
    val isConnected by lazy {
        MutableLiveData(true)
    }

    fun startUdpConnection(): String {
        return udpConnection.startUdpConnection()
    }

    fun startConnection(ip: String) {
        tcpConnection.startTcpConnection(ip)
    }

    fun sendConnect(id: String, userName: String) {
        tcpConnection.sendConnect(id, userName)
    }

    fun sendPing(id: String) {
        tcpConnection.sendPing(id)
    }

    fun sendGetUsers(id: String) {
        tcpConnection.sendGetUsers(id)
    }

    suspend fun getUsers(): List<User> {
        return databaseDao.getUsers()
    }

    suspend fun getMessages(): List<Message> {
        return databaseDao.getMessagesList()
    }

    suspend fun clearDb() {
        databaseDao.deleteUsers()
        databaseDao.deleteMessages()
    }

    suspend fun sendMessage(id: String, receiver: String, message: String) {
        tcpConnection.sendMessage(id, receiver, message)
        databaseDao.addMessage(Message(UUID.randomUUID().toString(), id, receiver, message))
    }

    fun sendDisconnect(id: String, code: Int) {
        DI.id.postValue("")
        tcpConnection.sendDisconnect(id, code)
        tcpConnection.close()
        GlobalScope.launch {
            isConnected.postValue(false)
        }
    }

    suspend fun receiveAnswer() {
        tcpConnection.receiveAnswer()
        DI.messageFromServerFlow.collect { baseDto ->
            when (baseDto.action) {
                BaseDto.Action.CONNECTED -> {
                    val connectedDto = Gson().fromJson(baseDto.payload, ConnectedDto::class.java)
                    DI.id.postValue(connectedDto.id)
                }
                BaseDto.Action.USERS_RECEIVED -> {
                    val usersReceivedDto =
                        Gson().fromJson(baseDto.payload, UsersReceivedDto::class.java)
                    usersReceivedDto.users.forEach { _ ->
                        Log.d("test", "${usersReceivedDto.users}")
                    }
                    databaseDao.addUsers(usersReceivedDto.users)
                    Log.d("test", "add to database")
                }
                BaseDto.Action.NEW_MESSAGE -> {
                    val messageDto = Gson().fromJson(baseDto.payload, MessageDto::class.java)
                    val message = Message(
                        UUID.randomUUID().toString(),
                        messageDto.from.id,
                        DI.id.value!!,
                        messageDto.message
                    )
                    databaseDao.addMessage(message)
                }
                BaseDto.Action.PONG -> {
                    isConnected.postValue(true)
                }
            }
        }
    }
}
