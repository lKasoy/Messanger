package com.example.messenger.di

import androidx.lifecycle.MutableLiveData
import com.example.messenger.repository.servermodel.BaseDto
import kotlinx.coroutines.flow.MutableSharedFlow

object DI {

    val id by lazy {
        MutableLiveData<String>()
    }

    val messageFromServerFlow by lazy {
        MutableSharedFlow<BaseDto>()
    }
}