package com.practice.whatsappclone


import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ChatViewModel(private val chatId: String) : ViewModel() {
    val messages = mutableStateListOf<Message>()
    private val repo = ChatRepository()
    var newMessageText: String = ""

    init {
        repo.getMessages(chatId) {
            messages.clear()
            messages.addAll(it)
        }
    }

    fun sendMessage(receiverId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val timestamp = System.currentTimeMillis()
        val messageId = repo.generateMessageId(chatId)
        val msg = Message(
            messageId = messageId,
            chatId = chatId,
            senderId = userId,
            receiverId = receiverId,
            content = newMessageText,
            timeStamp = timestamp
        )
        repo.sendMessage(chatId, msg) { success, _ ->
            if (success) newMessageText = ""
        }
    }
}
