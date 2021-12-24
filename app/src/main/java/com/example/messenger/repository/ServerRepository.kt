package com.example.messenger.repository

import android.util.Log
import com.example.messenger.model.*
import com.example.messenger.model.BaseDto.Action.*
import com.google.gson.Gson
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket

class ServerRepository {

    private var socket = DatagramSocket()
    private val buffer = ByteArray(256)
    private lateinit var id: String

    private lateinit var clientSocket: Socket
    private lateinit var reader: BufferedReader
    private lateinit var writer: PrintWriter

    fun startUdpConnection(): String {
        Log.d("test", "start udp connection")
        try {
            socket.soTimeout = 3000
            var packet = DatagramPacket(
                buffer, buffer.size,
                InetAddress.getByName("255.255.255.255"), 8888
            )
            socket.send(packet)
            packet = DatagramPacket(buffer, buffer.size)
            socket.receive(packet)
            val ip = packet.address.hostAddress
            Log.d("test", ip as String)
            return ip
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("test", e.toString())
            return ""
        }
    }

    fun startTCPIPConnection(ip: String) {
        Log.d("test", "start tcpip connection")
        clientSocket = Socket(ip, 6666)
        reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        writer = PrintWriter(OutputStreamWriter(clientSocket.getOutputStream()))
        val response = reader.readLine()
        val connectedDto = jsonToPayload(response) as ConnectedDto
        id = connectedDto.id
        Log.d("test", "id = $id")
    }

    fun getId(): String {
        Log.d("test", "getId")
        return id
    }

    fun sendConnect(id: String, userName: String) {
        writer.println(sendToServer(CONNECT, ConnectDto(id, userName)))
        writer.flush()
        Log.d("test", "send connect username = $userName")
    }

    fun sendPing(id: String): Boolean {
        writer.println(sendToServer(PING, PingDto(id)))
        writer.flush()
        Log.d("test", "send ping")
        val pongDto = reader.readLine()
        Log.d("test", "pongDto - $pongDto")
        return pongDto != null
    }

    fun getUsers(id: String): List<User> {
        writer.println(sendToServer(GET_USERS, GetUsersDto(id)))
        writer.flush()
        val users = reader.readLine()
        val usersReceivedDto = jsonToPayload(users) as UsersReceivedDto
        Log.d("test","${usersReceivedDto.users}")
        return usersReceivedDto.users
    }

    fun sendMessage(id: String, receiver: String, message: String) {
        Log.d("test","send message")
        writer.println(sendToServer(SEND_MESSAGE, SendMessageDto(id, receiver, message)))
        Log.d("test","message - $message")
    }

    fun disconnect(id: String, code: Int) {
        Log.d("test","disconnect")
        writer.println(sendToServer(DISCONNECT, DisconnectDto(id, code)))
    }

    private fun sendToServer(action: BaseDto.Action, payload: Payload): String? {
        return when (action) {
            CONNECT -> {
                payload as ConnectDto
                Gson().toJson(BaseDto(CONNECT, Gson().toJson(ConnectDto(payload.id, payload.name))))
            }
            PING -> {
                payload as PingDto
                Gson().toJson(BaseDto(PING, Gson().toJson(PingDto(payload.id))))
            }
            GET_USERS -> {
                payload as GetUsersDto
                Gson().toJson(BaseDto(GET_USERS, Gson().toJson(GetUsersDto(payload.id))))
            }
            SEND_MESSAGE -> {
                payload as SendMessageDto
                Gson().toJson(BaseDto(SEND_MESSAGE, Gson().toJson(SendMessageDto(payload.id, payload.receiver, payload.message))))
            }
            else -> {
                return null
            }
        }
    }

    private fun jsonToPayload(response: String): Payload {

        val jsonObject = JSONObject(response)
        val action = jsonObject.getString("action")
        val payload = jsonObject.getString("payload")
        return when (action) {
            "CONNECTED" -> {
                return Gson().fromJson(payload, ConnectedDto::class.java)
            }
            "PONG" -> {
                return Gson().fromJson(payload, PongDto::class.java)
            }
            "USERS_RECEIVED" -> {
                return Gson().fromJson(payload, UsersReceivedDto::class.java)
            }
            "NEW_MESSAGE" -> {
                return Gson().fromJson(payload, MessageDto::class.java)
            }
            else -> {
                Error(jsonObject.getString("payload"))
            }
        }
    }
}