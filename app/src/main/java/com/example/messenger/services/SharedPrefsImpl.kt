package com.example.messenger.services

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.example.messenger.services.constants.Constants

class SharedPrefsImpl(private val context: Context) : SharedPrefs {

    override fun saveUser(userName: String) {
        val savedPref: SharedPreferences =
            context.getSharedPreferences(Constants.ID_PREFS, AppCompatActivity.MODE_PRIVATE)
                ?: return
        with(savedPref.edit()) {
            putString(Constants.USERNAME, userName)
            apply()
        }
    }

    override fun getUserName(): String {
        val sharedPrefs =
            context.getSharedPreferences(Constants.ID_PREFS, AppCompatActivity.MODE_PRIVATE)
        return sharedPrefs.getString(Constants.USERNAME, "")!!
    }

    override fun resetUserName() {
        val savedPref: SharedPreferences =
            context.getSharedPreferences(Constants.ID_PREFS, AppCompatActivity.MODE_PRIVATE)
                ?: return
        with(savedPref.edit()) {
            putString(Constants.USERNAME, "")
            apply()
        }
    }
}