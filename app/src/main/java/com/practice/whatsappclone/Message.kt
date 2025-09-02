package com.practice.whatsappclone

data class Message(
    val messageId: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val mediaUrl: String? = null,
    val type: String = "text", // "text", "image", "audio", "video"
    val timeStamp: Long = 0L,
    val isSeen: Boolean = false,
)