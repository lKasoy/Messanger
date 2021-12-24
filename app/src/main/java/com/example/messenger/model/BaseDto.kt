package com.example.messenger.model

data class BaseDto(val action: Action, val payload: String) {

    enum class Action {
        PING, PONG, CONNECT, CONNECTED, GET_USERS, USERS_RECEIVED, SEND_MESSAGE, NEW_MESSAGE, DISCONNECT
    }

}