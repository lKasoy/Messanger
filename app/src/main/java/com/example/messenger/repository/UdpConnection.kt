package com.example.messenger.repository

import android.util.Log
import com.example.messenger.services.constants.Constants.HOST_IP
import com.example.messenger.services.constants.Constants.UDP_PORT
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpConnection : UdpConnectionSample {

    override fun startUdp(): String {
        val socket = DatagramSocket()
        val buffer = ByteArray(256)
        var ip: String? = ""
        Log.d("test", "start udp connection")
        while (ip == "") {
            try {
                socket.soTimeout = 3000
                var packet = DatagramPacket(
                    buffer, buffer.size,
                    InetAddress.getByName(HOST_IP), UDP_PORT
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
