package com.practice.whatsappclone


import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
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

        // Save FCM token to Firestore silently
        saveFcmTokenToFirestore()

        setContent {
            WhatsAppCloneTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                // Decide start destination
                val startDestination =
                    if (authViewModel.checkLogin()) "home" else "login"

                WhatsAppNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    startDestination = startDestination
                )
            }
        }
    }

    private fun saveFcmTokenToFirestore() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result ?: return@addOnCompleteListener
                val userId = firebaseAuth.currentUser?.uid ?: return@addOnCompleteListener

                firestore.collection("users")
                    .document(userId)
                    .update("fcmTokens", FieldValue.arrayUnion(token))
            } else {
                task.exception?.printStackTrace()
            }
        }
    }

    private fun sendNotificationToUser(receiverToken: String, title: String, body: String) {
        NotificationHelper.sendPushNotification(this, receiverToken, title, body)
    }
}
