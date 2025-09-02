package com.practice.whatsappclone

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChatScreen(chatId: String, receiverId: String) {
    val context = LocalContext.current
    val chatViewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(chatId))
    val messages = chatViewModel.messages
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .systemBarsPadding()
        ) {
            items(messages) { message ->
                MessageBubble(
                    isSender = message.senderId == currentUserId,
                    message = message
                )
            }
        }

        MessageInputBar(
            onSendMessage = { content, mediaUrl, type ->
                coroutineScope.launch {
                    chatViewModel.sendMessage(
                        receiverId = receiverId,
                        content = content,
                        mediaUrl = mediaUrl,
                        type = type,
                        context = context // Pass context here
                    )
                }
            }
        )
    }
}

@Composable
fun MessageBubble(isSender: Boolean, message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isSender) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 4.dp,
            modifier = Modifier
                .widthIn(max = 250.dp)
        ) {
            if (message.type == "image" && !message.mediaUrl.isNullOrBlank()) {
                AsyncImage(
                    model = message.mediaUrl,
                    contentDescription = "Image message",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(8.dp)
                )
            } else {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(12.dp),
                    color = if (isSender) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}
