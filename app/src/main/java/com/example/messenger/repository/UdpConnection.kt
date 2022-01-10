package com.example.messenger.repository

import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpConnection {

     fun startUdpConnection(): String {
        val socket = DatagramSocket()
        val buffer = ByteArray(256)
        var ip: String? = ""

        Log.d("test", "start udp connection")
        while (ip == "") {
            try {
                socket.soTimeout = 3000
                var packet = DatagramPacket(
                    buffer, buffer.size,
                    InetAddress.getByName("255.255.255.255"), 8888
                )
                socket.send(packet)
                packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)
                ip = packet.address.hostAddress
                Log.d("test", ip as String)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("test", e.toString())
            }
        }
        socket.close()
        return ip!!
    }
}
