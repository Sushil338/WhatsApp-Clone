package com.practice.whatsappclone

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class AuthViewModel(
    private val repository : AuthRepository = AuthRepository()
) : ViewModel(){
    var phoneNumber by mutableStateOf("")
    var verificationId by mutableStateOf("")
    var otp by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMsg by mutableStateOf<String?>(null)

    fun checkLogin() = repository.isUserLoggedIn()

    fun startPhoneAuth(activity: Activity, onCodeSent: () -> Unit) {
        isLoading = true
        repository.sendVerificationCode(
            phoneNumber, activity,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    /*TODO("Not yet implemented")*/
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    isLoading = false
                    errorMsg = e.message
                }

                override fun onCodeSent(vid: String, token: PhoneAuthProvider.ForceResendingToken) {
                    verificationId = vid
                    isLoading = false
                    onCodeSent()
                }
            })
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        isLoading = true
        repository.verifyCode(verificationId, otp){ success, message ->
            isLoading = false
            if(success) onSuccess()
            else errorMsg = message

        }
    }


}