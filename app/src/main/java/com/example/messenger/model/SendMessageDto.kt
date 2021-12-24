package com.example.messenger.model

data class SendMessageDto(val id: String, val receiver: String, val message: String) : Payload