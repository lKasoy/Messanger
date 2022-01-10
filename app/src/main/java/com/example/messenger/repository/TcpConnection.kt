package com.example.messenger.repository

import android.util.Log
import com.example.messenger.di.DI
import com.example.messenger.repository.servermodel.*
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket

class TcpConnection {

    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null

    fun startTcpConnection(ip: String) {
        Log.d("test", "start tcp")
        socket = Socket(ip, 6666)
        writer = PrintWriter(OutputStreamWriter(socket?.getOutputStream()))
        reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
    }

    fun sendConnect(id: String, userName: String) {
        writer?.println(sendToServer(BaseDto.Action.CONNECT, ConnectDto(id, userName)))
        writer?.flush()
        Log.d("test", "send connect")
    }

    fun sendPing(id: String) {
        writer?.println(sendToServer(BaseDto.Action.PING, PingDto(id)))
        writer?.flush()
        Log.d("test", "send ping")
    }

    fun sendGetUsers(id: String) {
        writer?.println(sendToServer(BaseDto.Action.GET_USERS, GetUsersDto(id)))
        writer?.flush()
        Log.d("test", "send get Users")
    }

    fun sendMessage(id: String, receiver: String, message: String) {
        Log.d("test", "send message")
        Log.d("test", "$id, $receiver, $message")
        writer?.println(
            sendToServer(
                BaseDto.Action.SEND_MESSAGE,
                SendMessageDto(id, receiver, message)
            )
        )
        Log.d("test", "message - $message")
    }

    fun sendDisconnect(id: String, code: Int) {
        writer?.println(sendToServer(BaseDto.Action.DISCONNECT, DisconnectDto(id, code)))
        Log.d("test", "disconnected from the server")
    }

    private fun sendToServer(action: BaseDto.Action, payload: Payload): String? {
        return when (action) {
            BaseDto.Action.CONNECT -> {
                payload as ConnectDto
                Gson().toJson(
                    BaseDto(
                        BaseDto.Action.CONNECT,
                        Gson().toJson(ConnectDto(payload.id, payload.name))
                    )
                )
            }
            BaseDto.Action.PING -> {
                payload as PingDto
                Gson().toJson(BaseDto(BaseDto.Action.PING, Gson().toJson(PingDto(payload.id))))
            }
            BaseDto.Action.GET_USERS -> {
                payload as GetUsersDto
                Gson().toJson(
                    BaseDto(
                        BaseDto.Action.GET_USERS,
                        Gson().toJson(GetUsersDto(payload.id))
                    )
                )
            }
            BaseDto.Action.SEND_MESSAGE -> {
                payload as SendMessageDto
                Gson().toJson(
                    BaseDto(
                        BaseDto.Action.SEND_MESSAGE,
                        Gson().toJson(
                            SendMessageDto(
                                payload.id,
                                payload.receiver,
                                payload.message
                            )
                        )
                    )
                )
            }
            BaseDto.Action.DISCONNECT -> {
                payload as DisconnectDto
                Gson().toJson(
                    BaseDto(
                        BaseDto.Action.DISCONNECT,
                        Gson().toJson(DisconnectDto(payload.id, payload.code))
                    )
                )
            }
            else -> {
                null
            }
        }
    }

    fun receiveAnswer() {
        GlobalScope.launch {
            val running = true
            while (running) {
                try {
                    val message = reader?.readLine()
                    DI.messageFromServerFlow.emit(jsonToPayload(message!!))
                    Log.d(
                        "test",
                        "Action - ${jsonToPayload(message).action}, Payload - ${jsonToPayload(message).payload}"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun jsonToPayload(response: String): BaseDto {
        val jsonObject = JSONObject(response)
        val action = jsonObject.getString("action")
        val payload = jsonObject.getString("payload")
        when (action) {
            "CONNECTED" -> {
                return BaseDto(BaseDto.Action.CONNECTED, payload)
            }
            "PONG" -> {
                return BaseDto(BaseDto.Action.PONG, payload)
            }
            "USERS_RECEIVED" -> {
                return BaseDto(BaseDto.Action.USERS_RECEIVED, payload)
            }
            "NEW_MESSAGE" -> {
                return BaseDto(BaseDto.Action.NEW_MESSAGE, payload)
            }
            else -> {
                return BaseDto(BaseDto.Action.DISCONNECT, payload)
            }
        }
    }

    fun close() {
        socket?.close()
        writer?.close()
        reader?.close()
    }
}