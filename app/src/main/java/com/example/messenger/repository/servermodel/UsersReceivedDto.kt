package com.example.messenger.repository.servermodel

data class UsersReceivedDto(val users: List<User>) : Payload

data class User(
    val id: String,
    var name: String
)
