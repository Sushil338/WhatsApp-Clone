package com.practice.whatsappclone

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UserRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    fun saveProfile(
        userId: String,
        name: String,
        about: String?,
        bio: String?,
        photoUri: Uri?,
        onComplete: (Boolean, String?) -> Unit
    ) {
        if (photoUri != null) {
            val ref = storage.reference.child("profile_images/$userId.jpg")
            ref.putFile(photoUri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) throw task.exception ?: Exception("Upload failed")
                    ref.downloadUrl
                }
                .addOnSuccessListener { uri ->
                    val profile = mutableMapOf(
                        "name" to name,
                        "photoUri" to uri.toString()
                    )
                    about?.let { profile["about"] = it }
                    bio?.let { profile["bio"] = it }
                    db.collection("users").document(userId).set(profile)
                        .addOnSuccessListener { onComplete(true, null) }
                        .addOnFailureListener { onComplete(false, it.message) }
                }
                .addOnFailureListener { onComplete(false, it.message) }
        } else {
            val profile = mutableMapOf<String, Any>(
                "name" to name
            )
            about?.let { profile["about"] = it }
            bio?.let { profile["bio"] = it }
            db.collection("users").document(userId).set(profile)
                .addOnSuccessListener { onComplete(true, null) }
                .addOnFailureListener { onComplete(false, it.message) }
        }
    }
}
