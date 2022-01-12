package com.example.messenger.repository.servermodel

data class MessageDto(val from: User, val message: String) : Payload