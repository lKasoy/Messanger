package com.example.messenger.repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.messenger.repository.db.entitydb.User
import com.example.messenger.repository.db.entitydb.Message

@Database(entities = [User::class, Message::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract val getDatabaseDao: DatabaseDao
}