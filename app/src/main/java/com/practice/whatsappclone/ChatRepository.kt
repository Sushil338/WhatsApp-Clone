package com.practice.whatsappclone

import com.google.firebase.firestore.FirebaseFirestore

class ChatRepository (
    private  val db: FirebaseFirestore = FirebaseFirestore.getInstance()
){

    fun generateMessageId(chatId: String): String {
        return db.collection("chats")
            .document(chatId)
            .collection("messages")
            .document()
            .id
    }

    fun sendMessage(chatId: String, message: Message, onComplete: (Boolean, String?) -> Unit){
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(message.messageId)
            .set(message)
            .addOnSuccessListener { onComplete(true, null)}
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }
    fun getMessages(chatId: String, onResult: (List<Message>) -> Unit){
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timeStamp")
            .addSnapshotListener { snapshot, error ->
                if(error != null || snapshot == null)
                    return@addSnapshotListener
                val messages =snapshot.documents.mapNotNull{
                    it.toObject(Message::class.java)
                }
                onResult(messages)
            }
    }
}