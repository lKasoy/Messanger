package com.example.messenger.repository.servermodel

import com.example.messenger.repository.db.entitydb.User

data class UsersReceivedDto(val users: List<User>) : Payload

