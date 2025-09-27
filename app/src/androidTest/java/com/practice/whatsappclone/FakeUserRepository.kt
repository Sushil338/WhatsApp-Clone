package com.practice.whatsappclone

import android.net.Uri

class FakeUserRepository : UserRepository() {

    // Instead of returning a User object, return a Map for test purposes
    fun getUserById(userId: String): Map<String, Any?> {
        return mapOf(
            "id" to userId,
            "name" to "Test User",
            "email" to "test@example.com",
            "profilePicture" to null
        )
    }

    override fun saveProfile(
        userId: String,
        name: String,
        about: String?,
        bio: String?,
        photoUri: Uri?,
        onComplete: (Boolean, String?) -> Unit
    ) {
        // Fake save: always succeed
        onComplete(true, null)
    }
}
