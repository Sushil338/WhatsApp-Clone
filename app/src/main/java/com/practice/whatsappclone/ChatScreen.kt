package com.practice.whatsappclone

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChatScreen(chatId: String, receiverId: String) {
    val chatViewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(chatId))
    val messages = chatViewModel.messages

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            items(messages) { message ->
                MessageBubble(
                    isSender = message.senderId == FirebaseAuth.getInstance().currentUser?.uid,
                    text = message.content
                )
            }
        }

        Row(modifier = Modifier.padding(8.dp)) {
            OutlinedTextField(
                value = chatViewModel.newMessageText,
                onValueChange = { chatViewModel.newMessageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") }
            )
            IconButton(
                onClick = { chatViewModel.sendMessage(receiverId) },
                enabled = chatViewModel.newMessageText.isNotBlank()
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun MessageBubble(isSender: Boolean, text: String) {
    Surface(
        color = if (isSender) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text, modifier = Modifier.padding(12.dp))
    }
}
