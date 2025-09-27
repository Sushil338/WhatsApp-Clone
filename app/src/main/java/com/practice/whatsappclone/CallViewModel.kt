package com.practice.whatsappclone

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

data class Call(
    val callId: String = "",
    val callerUid: String = "",
    val calleeUid: String = "",
    val contactName: String = "",
    val time: Long = 0L,
    val type: String = "", // audio or video or Outgoing/Incoming
    val status: String = "", // ringing, accepted, ended, missed
    val duration: Long = 0L // Call duration in milliseconds (optional)
)

class CallViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // Compose state lists and mutable states
    private val _calls = mutableStateListOf<Call>()
    val calls: List<Call> get() = _calls

    val error = mutableStateOf<String?>(null)

    private var callStatusListener: ListenerRegistration? = null

    init {
        loadCallLogs()
    }

    /** Observe a call's status in real-time */
    fun observeCallStatus(callId: String, onStatusChanged: (String) -> Unit) {
        callStatusListener?.remove()
        val callDoc = db.collection("calls").document(callId)
        callStatusListener = callDoc.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("CallViewModel", "Call status listen error", error)
                return@addSnapshotListener
            }
            snapshot?.getString("status")?.let { status ->
                onStatusChanged(status)
            }
        }
    }

    /** Remove listener to prevent leaks */
    fun removeCallStatusListener() {
        callStatusListener?.remove()
        callStatusListener = null
    }

    /** Load all calls from Firestore, sorted by time descending */
    private fun loadCallLogs() {
        db.collection("calls")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, exception ->
                if (exception != null) {
                    error.value = "Failed to load call logs: ${exception.message}"
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    _calls.clear()
                    for (doc in snapshots.documents) {
                        val call = doc.toObject(Call::class.java)
                        if (call != null) {
                            _calls.add(call)
                        }
                    }
                }
            }
    }

    /** Fetch the latest FCM token of the callee */
    private suspend fun getCalleeFcmToken(calleeUid: String): String? = withContext(Dispatchers.IO) {
        try {
            val doc = db.collection("users").document(calleeUid).get().await()
            val tokens = doc.get("fcmTokens") as? List<*>
            tokens?.lastOrNull() as? String
        } catch (e: Exception) {
            Log.e("CallViewModel", "Error fetching callee FCM token", e)
            null
        }
    }

    /** Send call notification via external API */
    private suspend fun sendCallNotification(
        calleeToken: String,
        callId: String,
        callerName: String,
        callType: String = "video"
    ) = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val json = """
            {
              "token": "$calleeToken",
              "callId": "$callId",
              "callerName": "$callerName",
              "callType": "$callType"
            }
        """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://vercelnotifications-ph8hp5jrg-sushils-projects-d3bdb4d0.vercel.app/send-notification")
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("Failed to send notification: ${response.message}")
        }
    }

    /** Initiate a new call and save to Firestore */
    fun initiateCall(calleeUid: String, calleeName: String, callId: String) {
        val callerUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val callerName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Caller"
        val timeNow = System.currentTimeMillis()
        val callData = mapOf(
            "callId" to callId,
            "callerUid" to callerUid,
            "callerName" to callerName,
            "calleeUid" to calleeUid,
            "calleeName" to calleeName,
            "time" to timeNow,
            "type" to "Outgoing",
            "status" to "ringing"
        )
        db.collection("calls").document(callId).set(callData)
            .addOnSuccessListener {
                Log.d("CallViewModel", "Call document created with ID $callId")
                viewModelScope.launch {
                    val token = getCalleeFcmToken(calleeUid)
                    if (token != null) {
                        try {
                            sendCallNotification(token, callId, callerName, "video")
                            Log.d("CallViewModel", "Call notification sent")
                        } catch (e: Exception) {
                            Log.e("CallViewModel", "Failed to send call notification", e)
                        }
                    } else {
                        Log.w("CallViewModel", "No FCM token for callee")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("CallViewModel", "Failed to create call document", e)
                error.value = "Failed to create call: ${e.message}"
            }
    }

    /** Update call status (e.g., accepted, ended, missed) */
    fun updateCallStatus(callId: String, status: String) {
        db.collection("calls").document(callId).update("status", status)
            .addOnSuccessListener {
                Log.d("CallViewModel", "Updated call status to $status")
            }
            .addOnFailureListener { e ->
                Log.e("CallViewModel", "Failed to update call status", e)
                error.value = "Failed to update call status: ${e.message}"
            }
    }
}
