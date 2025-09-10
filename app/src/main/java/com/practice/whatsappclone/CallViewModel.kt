package com.practice.whatsappclone

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class Call(
    val id: String,
    val contactName: String,
    val time: String,
    val type: String = "Missed"
)

class CallViewModel : ViewModel() {
    val calls = mutableStateListOf<Call>()
    val error = mutableStateOf<String?>(null)

    init {
        loadCalls()
    }

    private fun loadCalls() {
        try {
            calls.clear()
            calls.addAll(
                listOf(
                    Call("1", "Alice", "Today, 11:15 AM", "Incoming"),
                    Call("2", "Bob", "Yesterday, 8:30 PM", "Outgoing"),
                    Call("3", "Charlie", "Yesterday, 3:41 PM", "Missed")
                )
            )
        } catch (e: Exception) {
            error.value = "Failed to load call history: ${e.message}"
        }
    }
}
