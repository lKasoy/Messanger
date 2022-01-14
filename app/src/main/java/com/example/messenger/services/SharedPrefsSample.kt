package com.example.messenger.services

interface SharedPrefsSample {

    fun saveUser(userName: String)

    fun getUserName(): String

    fun resetUserName()
}