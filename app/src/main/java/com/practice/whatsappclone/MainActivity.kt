package com.practice.whatsappclone

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.practice.whatsappclone.ui.theme.WhatsAppCloneTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Save FCM token to Firestore
        saveFcmTokenToFirestore()

        setContent {
            WhatsAppCloneTheme {
                val navController = rememberNavController()
                val authViewModel = remember { AuthViewModel() }
                WhatsAppNavGraph(navController, authViewModel)
            }
        }
    }

    /**
     * Save the device FCM token in Firestore under the current user's document.
     * This allows sending push notifications to this device.
     */
    private fun saveFcmTokenToFirestore() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result ?: return@addOnCompleteListener
                val userId = firebaseAuth.currentUser?.uid ?: return@addOnCompleteListener

                Toast.makeText(this, "FCM Token: $token", Toast.LENGTH_LONG).show()

                firestore.collection("users")
                    .document(userId)
                    .update("fcmTokens", FieldValue.arrayUnion(token))
                    .addOnSuccessListener {
                        println("FCM token saved successfully")
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            } else {
                task.exception?.printStackTrace()
            }
        }
    }

    /**
     * Example usage of sending a notification via Vercel server.
     * Call this after sending a message to another user.
     */
    private fun sendNotificationToUser(receiverToken: String, title: String, body: String) {
        NotificationHelper.sendPushNotification(this, receiverToken, title, body)
    }
}
