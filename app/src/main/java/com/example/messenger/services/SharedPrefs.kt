package com.example.messenger.services

interface SharedPrefs {

    fun saveUser(userName: String)

    fun getUserName(): String

    fun resetUserName()
}