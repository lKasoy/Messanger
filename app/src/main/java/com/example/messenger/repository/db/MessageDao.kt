package com.example.messenger.repository.db

import com.example.messenger.repository.db.entitydb.Message

interface MessageDao {

    suspend fun addMessage(message: Message)

    suspend fun getAllMessages(): List<Message>

    suspend fun deleteAllMessages()
}