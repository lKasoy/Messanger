package com.example.messenger.repository

import com.example.messenger.model.User

class DecoratorRepository(private val serverRepository: ServerRepository) {

    fun startUdp(): String {
        return serverRepository.startUdpConnection()
    }

    fun startTCPIP(ip: String) {
        serverRepository.startTCPIPConnection(ip)
    }

    fun getId(): String {
        return serverRepository.getId()
    }

    fun connect(id: String, userName: String){
        serverRepository.sendConnect(id, userName)
    }

    fun sendPing(id: String): Boolean {
        return serverRepository.sendPing(id)
    }

    fun getUsers(id: String): List<User> {
        return serverRepository.getUsers(id)
    }

    fun sendMessage(id: String, receiver: String, message: String) {
        serverRepository.sendMessage(id, receiver, message)
    }

    fun logOut(id: String, code: Int){
        serverRepository.disconnect(id, code)
    }
}