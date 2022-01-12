package com.example.messenger.repository

import android.util.Log
import com.example.messenger.repository.db.entitydb.Message
import com.example.messenger.repository.servermodel.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket

class TcpConnection {

    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private var id: String? = null

    val newMessage by lazy {
        MutableSharedFlow<MessageDto>()
    }

    val users by lazy {
        MutableSharedFlow<UsersReceivedDto>()
    }

    private val isConnection by lazy {
        MutableStateFlow(true)
    }

    var user: User? = null

    fun startTcpConnection(ip: String) {
        socket = Socket(ip, 6666)
        writer = PrintWriter(OutputStreamWriter(socket?.getOutputStream()))
        reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
        val baseDto = jsonToPayload(reader?.readLine()!!)
        val connectedDto = Gson().fromJson(baseDto.payload, ConnectedDto::class.java)
        id = connectedDto.id
        Log.d("test", "start tcp id = $id")
    }

    fun sendConnect(userName: String) {
        writer?.println(sendToServer(BaseDto.Action.CONNECT, ConnectDto(id!!, userName)))
        writer?.flush()
        user = User(id!!, userName)
        Log.d("test", "send connect")
        scope.launch {
            isConnection.emit(true)
        }
        sendPing()
    }


    private fun sendPing() {
        scope.launch {
            while (socket?.isClosed == false && isConnection.value) {
                writer?.println(sendToServer(BaseDto.Action.PING, PingDto(id!!)))
                writer?.flush()
                delay(9000)
                Log.d("test", "send ping")
            }
        }
        receiveAnswer()
    }

    fun sendGetUsers() {
        scope.launch {
            writer?.println(sendToServer(BaseDto.Action.GET_USERS, GetUsersDto(id!!)))
            writer?.flush()
            Log.d("test", "send get Users")
        }
    }

    fun sendMessage(message: Message) {
        writer?.println(
            sendToServer(
                BaseDto.Action.SEND_MESSAGE,
                SendMessageDto(
                    id = message.senderId,
                    receiver = message.receiverId,
                    message = message.message
                )
            )
        )
        Log.d("test", "send message to ${message.receiverId}, message - ${message.message}")
    }

    private fun receiveAnswer() {
        scope.launch {
            while (socket?.isClosed == false && isConnection.value) {
                try {
                    val baseDto = jsonToPayload(reader?.readLine()!!)
                    when (baseDto.action) {
                        BaseDto.Action.PONG -> {
                            isConnection.emit(true)
                            Log.d("test", "scope PONG ${baseDto.payload}")
                        }
                        BaseDto.Action.USERS_RECEIVED -> {
                            users.emit(
                                Gson().fromJson(
                                    baseDto.payload,
                                    UsersReceivedDto::class.java
                                )
                            )
                            Log.d("test", "scope  USERS_RECEIVED ${baseDto.payload}")
                        }
                        BaseDto.Action.NEW_MESSAGE -> {
                            newMessage.emit(
                                Gson().fromJson(
                                    baseDto.payload,
                                    MessageDto::class.java
                                )
                            )
                            Log.d("test", "scope NEW_MESSAGE ${baseDto.payload}")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("test", "$e")
                }
            }
        }
    }

    private fun sendToServer(action: BaseDto.Action, payload: Payload): String? {
        return Gson().toJson(
            BaseDto(
                action,
                Gson().toJson(payload)
            )
        )
    }

    private fun jsonToPayload(response: String): BaseDto {
        return Gson().fromJson(response, BaseDto::class.java)
    }

    fun sendDisconnect() {
        scope.launch {
            isConnection.emit(false)
            writer?.println(sendToServer(BaseDto.Action.DISCONNECT, DisconnectDto(id!!, 1)))

            id = ""
        }
        job.cancelChildren()
        socket?.close()
        writer?.close()
        reader?.close()
        Log.d("test", "disconnected from the server")
    }


    fun getCurrentUser(): User {
        Log.d("test", "${user!!.name} id - ${user!!.id}")
        return user!!
    }
}