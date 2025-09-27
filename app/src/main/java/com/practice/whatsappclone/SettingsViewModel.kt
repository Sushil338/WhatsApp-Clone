package com.practice.whatsappclone

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SettingsViewModel : ViewModel() {

    var error = mutableStateOf<String?>(null)

    fun performLogout(onLogoutComplete: () -> Unit) {
        try {
            // Firebase sign out
            FirebaseAuth.getInstance().signOut()
            // TODO: Clear additional cached data or ViewModels if needed
            onLogoutComplete()
        } catch (e: Exception) {
            error.value = e.message ?: "Logout failed"
        }
    }
}
