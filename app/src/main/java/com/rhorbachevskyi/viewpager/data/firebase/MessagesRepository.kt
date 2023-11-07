package com.rhorbachevskyi.viewpager.data.firebase

import com.rhorbachevskyi.viewpager.data.model.Message

interface MessagesRepository {
    fun addMessage(message: Message)
}