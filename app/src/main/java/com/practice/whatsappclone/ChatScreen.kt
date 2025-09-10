package com.practice.whatsappclone


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    receiverId: String,
    receiverName: String,
    navController: NavController,
    receiverProfileUrl: String? = null
) {
    val chatViewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(chatId))
    val messages = chatViewModel.messages
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val context = LocalContext.current

    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(messages) { isLoading = false }
    LaunchedEffect(chatViewModel.error) {
        error = chatViewModel.error
        chatViewModel.error = null
    }

    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                color = Color(0xFF075E54),
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!receiverProfileUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = receiverProfileUrl,
                                contentDescription = "Receiver Profile",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Receiver Profile",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = receiverName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Row {
                        IconButton(onClick = { /* TODO: Navigate to video call */ }) {
                            Icon(Icons.Default.VideoCall, "Video Call", tint = Color.White)
                        }
                        IconButton(onClick = { /* TODO: Navigate to audio call */ }) {
                            Icon(Icons.Default.Call, "Audio Call", tint = Color.White)
                        }
                        IconButton(onClick = { /* TODO: More options */ }) {
                            Icon(Icons.Default.MoreVert, "More Options", tint = Color.White)
                        }
                    }
                }
            }
        },
        bottomBar = {
            MessageInputBar { content, mediaUrl, type ->
                chatViewModel.sendMessage(
                    receiverId = receiverId,
                    content = content,
                    mediaUrl = mediaUrl,
                    type = type,
                    context = context
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF128C7E))
                    }
                }
                messages.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No messages yet. Start the conversation!",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                else -> {
                    ChatMessages(
                        messages = messages,
                        currentUserId = currentUserId
                    )
                }

            }
            ErrorSnackbar(error = error) { error = null }
        }
    }
}

@Composable
fun MessageBubble(isSender: Boolean, message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isSender) Color(0xFFDCF8C6) else Color.White,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 2.dp,
            modifier = Modifier.widthIn(max = 250.dp)
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
                    color = Color.Black
                )
            }
        }
    }
}