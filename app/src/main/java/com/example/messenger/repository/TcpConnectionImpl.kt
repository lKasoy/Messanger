package com.example.messenger.repository

import android.util.Log
import com.example.messenger.repository.servermodel.*
import com.example.messenger.services.constants.Constants.TCP_IP_PORT
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket

class TcpConnectionImpl : TcpConnection {

    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private var id: String? = null
    private var userName: String? = null

    override val isConnected = MutableStateFlow(false)
    override val userList = MutableSharedFlow<List<User>>()
    override val newMessage = MutableSharedFlow<MessageDto>()

    override fun startTcpConnection(ip: String, userName: String) {
        socket = Socket(ip, TCP_IP_PORT)
        writer = PrintWriter(OutputStreamWriter(socket?.getOutputStream()))
        reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
        Log.d("test", "startTCP")
        receiveAnswer()
        this.userName = userName
    }

    override fun sendConnect() {
        writer?.println(
            sendToServer(
                BaseDto.Action.CONNECT, ConnectDto(id!!, userName!!)
            )
        )
        writer?.flush()
        Log.d("test", "send connect id - $id ")
        sendPing()
    }

    override fun sendPing() {
        scope.launch {
            while (socket?.isClosed == false && isConnected.value) {
                delay(9000)
                writer?.println(sendToServer(BaseDto.Action.PING, PingDto(id!!)))
                writer?.flush()
                Log.d("test", "send ping")
            }
        }
    }

    override fun sendGetUsers() {
        scope.launch {
            writer?.println(sendToServer(BaseDto.Action.GET_USERS, GetUsersDto(id!!)))
            writer?.flush()
            Log.d("test", "send get Users")
        }
    }

    override fun sendMessage(userId: String, receiverId: String, message: String) {
        writer?.println(
            sendToServer(
                BaseDto.Action.SEND_MESSAGE,
                SendMessageDto(
                    id = userId,
                    receiver = receiverId,
                    message = message
                )
            )
        )
        Log.d("test", "send message to $receiverId, message - $message")
    }

    override fun receiveAnswer() {
        scope.launch {
            while (socket?.isClosed == false) {
                try {
                    val baseDto = jsonToPayload(reader?.readLine()!!)
                    when (baseDto.action) {
                        BaseDto.Action.CONNECTED -> {
                            val connectedDto =
                                Gson().fromJson(baseDto.payload, ConnectedDto::class.java)
                            id = connectedDto.id
                            sendConnect()
                            isConnected.emit(true)
                            Log.d("test", "scope CONNECTED ${baseDto.payload}")
                        }
                        BaseDto.Action.PONG -> {
                            isConnected.emit(true)
                            Log.d("test", "scope PONG ${baseDto.payload}")
                        }
                        BaseDto.Action.USERS_RECEIVED -> {
                            userList.emit(
                                Gson().fromJson(
                                    baseDto.payload,
                                    UsersReceivedDto::class.java
                                ).users
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
                    isConnected.emit(false)
                }
            }
        }
    }

    override fun sendToServer(action: BaseDto.Action, payload: Payload): String {
        return Gson().toJson(
            BaseDto(
                action,
                Gson().toJson(payload)
            )
        )
    }

    override fun jsonToPayload(response: String): BaseDto {
        return Gson().fromJson(response, BaseDto::class.java)
    }

    override fun sendDisconnect() {
        scope.launch {
            isConnected.emit(false)
            writer?.println(sendToServer(BaseDto.Action.DISCONNECT, DisconnectDto(id!!, 1)))
            id = ""
        }
        job.cancelChildren()
        socket?.close()
        writer?.close()
        reader?.close()
        Log.d("test", "disconnected from the server")
    }

    override fun getUserId(): String {
        return if (id != "") {
            id!!
        } else {
            ""
        }
    }
}