package com.practice.whatsappclone

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatViewModel(private val chatId: String) : ViewModel() {
    val messages = mutableStateListOf<Message>()
    private val repo = ChatRepository()

    var newMessageText by mutableStateOf("")
    private val firestore = FirebaseFirestore.getInstance()

    init {
        repo.getMessages(chatId) { msgs ->
            messages.clear()
            messages.addAll(msgs)
        }
    }

    fun sendMessage(
        receiverId: String,
        content: String,
        mediaUrl: String?,
        type: String,
        context: Context
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val timestamp = System.currentTimeMillis()
        val messageId = repo.generateMessageId(chatId)

        val msg = Message(
            messageId = messageId,
            chatId = chatId,
            senderId = userId,
            receiverId = receiverId,
            content = if (type == "text") content else "",
            mediaUrl = mediaUrl,
            type = type,
            timeStamp = timestamp
        )

        // Write message to Firestore
        firestore.collection("chats").document(chatId)
            .collection("messages")
            .document(messageId)
            .set(msg)
            .addOnSuccessListener {
                // Clear input field
                newMessageText = ""

                // Send push notification
                firestore.collection("users").document(receiverId).get()
                    .addOnSuccessListener { doc ->
                        val receiverTokens = doc.get("fcmTokens") as? List<String> ?: return@addOnSuccessListener
                        if (receiverTokens.isNotEmpty()) {
                            val token = receiverTokens[0]
                            NotificationHelper.sendPushNotification(
                                context = context,
                                receiverToken = token,
                                title = "New Message",
                                body = if (type == "text") content else "Sent an image"
                            )
                        }
                    }
            }
            .addOnFailureListener { e ->
                println("Failed to send message: ${e.message}")
            }
    }
}
