package com.practice.whatsappclone

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "whatsapp_clone_notifications"
        private const val CALLS_CHANNEL_ID = "calls_channel"
        private const val TAG = "FCM"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived called")

        // Handle notification payload (messages)
        remoteMessage.notification?.let {
            val title = it.title ?: "New Message"
            val body = it.body ?: ""
            sendNotification(title, body)
        }

        // Handle data payload (e.g., incoming calls)
        if (remoteMessage.data.isNotEmpty()) {
            val dataMap = remoteMessage.data
            val callId = dataMap["callId"]
            val callerName = dataMap["callerName"]
            val callType = dataMap["callType"]

            if (callId != null && callerName != null) {
                showIncomingCallNotification(callId, callerName, callType)
                return
            }

            // Generic fallback for data messages
            val title = dataMap["title"] ?: "New Message"
            val body = dataMap["body"] ?: ""
            sendNotification(title, body)
        }
    }

    private fun showIncomingCallNotification(callId: String, callerName: String, callType: String?) {
        // Intent to open CallActivity on answering
        val acceptIntent = Intent(this, CallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("callId", callId)
            putExtra("contactName", callerName)
            putExtra("callType", callType ?: "video")
            putExtra("launchedFromChat", false)
        }

        val acceptPendingIntent = PendingIntent.getActivity(
            this, 0, acceptIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for calls (Oreo+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CALLS_CHANNEL_ID,
                "Incoming Calls",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Incoming call notifications" }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, CALLS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_call) // Replace with your call icon
            .setContentTitle("Incoming ${callType ?: "Video"} Call")
            .setContentText("Call from $callerName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(acceptPendingIntent, true)
            .addAction(R.drawable.ic_call_accept, "Answer", acceptPendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))

        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for messages (Oreo+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "WhatsApp Clone Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "General notifications" }
            notificationManager.createNotificationChannel(channel)
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // Replace with your app icon
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "New FCM token generated: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // TODO: Implement actual logic to send/update token on your backend
        Log.d(TAG, "Sending token to server: $token")
    }
}
