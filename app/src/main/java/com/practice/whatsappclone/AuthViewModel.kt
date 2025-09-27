package com.practice.whatsappclone

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

open class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    open var phoneNumber by mutableStateOf("")
    open var verificationId by mutableStateOf("")
    open var otp by mutableStateOf("")
    open var isLoading by mutableStateOf(false)
    open var errorMsg by mutableStateOf<String?>(null)

    open fun checkLogin() = repository.isUserLoggedIn()

    open fun startPhoneAuth(activity: Activity, onCodeSent: () -> Unit) {
        isLoading = true
        repository.sendVerificationCode(
            phoneNumber,
            activity,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // You can implement auto-retrieval here if needed
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    isLoading = false
                    errorMsg = e.message
                }

                override fun onCodeSent(
                    vid: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationId = vid
                    isLoading = false
                    onCodeSent()
                }
            }
        )
    }

    open fun verifyOtp(onSuccess: () -> Unit) {
        isLoading = true
        repository.verifyCode(verificationId, otp) { success, message ->
            isLoading = false
            if (success) {
                onSuccess()
            } else {
                errorMsg = message
            }
        }
    }
}
