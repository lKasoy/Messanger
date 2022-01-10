package com.example.messenger.repository.servermodel

data class SendMessageDto(val id: String, val receiver: String, val message: String) : Payload