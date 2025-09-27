package com.practice.whatsappclone

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.practice.whatsappclone.AuthViewModel

// Fake ViewModel for testing, simulates AuthViewModel behavior
class FakeAuthViewModel : AuthViewModel() {

    // Override state properties for testing
    override var phoneNumber by mutableStateOf("")
    override var verificationId by mutableStateOf("")
    override var otp by mutableStateOf("")
    override var isLoading by mutableStateOf(false)
    override var errorMsg by mutableStateOf<String?>(null)

    // Simulate starting phone authentication
    override fun startPhoneAuth(activity: Activity, onCodeSent: () -> Unit) {
        isLoading = true
        // Simulate code sent immediately
        verificationId = "fake_verification_id"
        isLoading = false
        onCodeSent()
    }

    // Simulate verifying OTP
    override fun verifyOtp(onSuccess: () -> Unit) {
        isLoading = true
        // Immediately succeed
        isLoading = false
        onSuccess()
    }
}
