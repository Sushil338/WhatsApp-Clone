package com.practice.whatsappclone

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.practice.whatsappclone.ui.theme.WhatsAppCloneTheme

class MainActivity : ComponentActivity() {

    private var navController: NavController? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Save FCM token to Firestore silently
        saveFcmTokenToFirestore()

        setContent {
            WhatsAppCloneTheme {
                navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                // Decide start destination based on authentication status
                val startDestination =
                    if (authViewModel.checkLogin()) "home" else "login"

                WhatsAppNavGraph(
                    navController = navController!! as NavHostController,
                    authViewModel = authViewModel,
                    startDestination = startDestination
                )
            }
        }

        // Handle intent if app is launched via notification tap or deep link
        handleIncomingIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        intent?.let {
            val callId = it.getStringExtra("callId")
            val callerName = it.getStringExtra("callerName")
            val callType = it.getStringExtra("callType") ?: "video"
            if (callId != null && callerName != null) {
                // Navigate to incoming call screen safely
                navController?.navigate("incoming_call/$callId/$callerName/$callType")
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
}
