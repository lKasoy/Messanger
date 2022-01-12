package com.example.messenger.repository.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.messenger.repository.db.entitydb.Message

@Dao
interface DatabaseDao {

    @Query("SELECT * FROM messages")
    suspend fun getMessagesList(): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMessage(message: Message)

    @Query("DELETE FROM messages")
    suspend fun deleteMessages()
}