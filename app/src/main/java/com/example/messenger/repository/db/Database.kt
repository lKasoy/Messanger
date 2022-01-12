package com.example.messenger.repository.db

import com.example.messenger.repository.db.entitydb.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class Database(private val databaseDao: DatabaseDao) : MessageDao {

    override suspend fun addMessage(message: Message) {
        databaseDao.addMessage(message = message)
    }

    override suspend fun getAllMessages(): List<Message> {
        return databaseDao.getMessagesList()
    }

    override suspend fun deleteAllMessages() {
        databaseDao.deleteMessages()
    }
}