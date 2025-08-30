package com.practice.whatsappclone

data class Message(
    val messageId: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timeStamp: Long = 0L,
    val isSeen: Boolean = false,
    val type: String = "text" //"Text", "Image"
)