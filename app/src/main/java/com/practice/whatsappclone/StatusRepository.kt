package com.practice.whatsappclone

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class StatusRepository (
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
){

    fun postStatus(
        userId: String,
        userName : String,
        userProfileUrl : String,
        imageUri : Uri,
        onComplete: (Boolean, String?) -> Unit
    ){
        val statusId = db.collection("statuses").document().id
        val storageRef = storage.reference.child("statuses/${UUID.randomUUID()}.jpg")

        storageRef.putFile(imageUri).continueWithTask { task ->
            if(!task.isSuccessful) throw task.exception ?: Exception("Image Upload failed!!")
            storageRef.downloadUrl
        }.addOnSuccessListener {downloadUri ->
            val status = Status(
                statusId = statusId,
                userId = userId,
                userName = userName,
                userProfileUrl = userProfileUrl,
                imageUrl = downloadUri.toString(),
                timeStamp = System.currentTimeMillis()
            )
            db.collection("statuses").document(statusId).set(status)
                .addOnSuccessListener { onComplete(true, null) }
                .addOnFailureListener { onComplete(false, it.message) }
        }.addOnFailureListener { onComplete(false, it.message) }
    }

    fun listenToStatuses(onResult: (List<Status>) -> Unit) {
        db.collection("statuses")
            .orderBy("timeStamp",
                com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val all = snapshot.documents.mapNotNull { it.toObject(Status::class.java) }

                // Only show last 24 hours
                val now = System.currentTimeMillis()
                val recent = all.filter { now - it.timeStamp < 24 * 60 * 60 * 1000 }
                onResult(recent)
            }
    }

    fun getStatusById(statusId: String, onResult: (Status?) -> Unit) {
        db.collection("statuses").document(statusId)
            .get()
            .addOnSuccessListener { doc -> onResult(doc.toObject(Status::class.java)) }
            .addOnFailureListener { onResult(null) }
    }

}