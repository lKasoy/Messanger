package com.example.messenger.repository.servermodel

import com.example.messenger.repository.db.entitydb.User

data class MessageDto(val from: User, val message: String) : Payload