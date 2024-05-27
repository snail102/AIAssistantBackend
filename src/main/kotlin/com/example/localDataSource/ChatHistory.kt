package com.example.localDataSource

import com.example.models.ChatMessage
import com.example.models.MessageGpt

object ChatHistory {


    private val previousMessages: MutableList<MessageGpt> = mutableListOf()


    fun addMessage(newMessage: MessageGpt) {
        previousMessages.add(newMessage)
    }

    fun addAll(messages: List<MessageGpt>) {
        previousMessages.addAll(messages)
    }


    fun getPreviousMessages(): List<MessageGpt> = previousMessages

}