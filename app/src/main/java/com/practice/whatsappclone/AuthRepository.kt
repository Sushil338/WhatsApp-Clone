package com.practice.whatsappclone

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class AuthRepository(private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()){

    fun isUserLoggedIn() : Boolean = firebaseAuth.currentUser != null

    fun sendVerificationCode(
        phoneNumber : String,
        activity : Activity,
        callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ){
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyCode(verificationId: String, code: String, onComplete: (Boolean, String?) -> Unit){
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful, task.exception?.message)
            }
    }
}