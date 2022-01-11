package com.example.messenger.repository.db.entitydb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey
    val id: String,
    val senderName: String,
    val senderId: String,
    val receiverId: String,
    val receiverName: String,
    val message: String
)
