package com.practice.whatsappclone

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UserRepository (
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage : FirebaseStorage = FirebaseStorage.getInstance()
){
    fun saveProfile(userId: String, name: String, photoUri: Uri?, onComplete: (Boolean, String?) -> Unit) {
        if(photoUri != null){
            val ref = storage.reference.child("profile_images/$userId.jpg")
            ref.putFile(photoUri)
                .continueWithTask { ref.downloadUrl }
                .addOnSuccessListener { uri ->
                    val profile = mapOf("name" to name, "photoUri" to uri.toString())
                    db.collection("users").document(userId).set(profile)
                        .addOnSuccessListener { onComplete(true, null) }
                        .addOnFailureListener { onComplete(false, it.message) }
                }.addOnFailureListener { onComplete(false, it.message) }
        }
        else{
            val profile = mapOf("name" to name)
            db.collection("users").document(userId).set(profile)
                .addOnSuccessListener { onComplete(true, null) }
                .addOnFailureListener {onComplete(false, it.message)}
        }
    }
}