package com.example.messenger.model

data class MessageDto(val from: User, val message: String) : Payload