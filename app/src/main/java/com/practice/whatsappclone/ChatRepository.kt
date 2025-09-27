package com.practice.whatsappclone

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ChatRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    fun generateMessageId(chatId: String): String {
        return db.collection("chats")
            .document(chatId)
            .collection("messages")
            .document()
            .id
    }

    fun sendMessage(chatId: String, message: Message, onComplete: (Boolean, String?) -> Unit) {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(message.messageId)
            .set(message)
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    fun getMessages(chatId: String, onResult: (List<Message>) -> Unit) {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timeStamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null)
                    return@addSnapshotListener

                val messages = snapshot.documents.mapNotNull {
                    it.toObject(Message::class.java)
                }
                onResult(messages)
            }
    }

    fun uploadImage(
        chatId: String,
        imageUri: Uri,
        onComplete: (String?) -> Unit
    ) {
        val ref = storage.reference.child("chat_media/$chatId/${System.currentTimeMillis()}.jpg")
        ref.putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception ?: Exception("Upload failed")
                ref.downloadUrl
            }
            .addOnSuccessListener { uri ->
                onComplete(uri.toString())
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }
}
