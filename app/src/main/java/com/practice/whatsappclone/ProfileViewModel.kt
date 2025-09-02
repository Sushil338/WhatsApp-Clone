package com.practice.whatsappclone

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ProfileViewModel(
    private val userRepo: UserRepository = UserRepository()
) : ViewModel() {
    var name by mutableStateOf("")
    var about by mutableStateOf("")
    var bio by mutableStateOf("")
    var photoUri by mutableStateOf<Uri?>(null)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun saveUserProfile(userId: String, onProfileSaved: () -> Unit) {
        isLoading = true
        userRepo.saveProfile(userId, name, about, bio, photoUri) { success, msg ->
            isLoading = false
            error = msg
            if (success) onProfileSaved()
        }
    }
}
