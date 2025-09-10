package com.practice.whatsappclone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import coil.compose.AsyncImage

data class ChatData(
    val id: String,
    val receiverId: String,
    val name: String,
    val lastMessage: String,
    val time: String,
    val profileUrl: String? = null
)

@Composable
fun ChatListScreen(navController: NavController) {
    val sampleChats = listOf(
        ChatData("1", "Alice_uid", "Alice", "Hey there!", "10:00 AM"),
        ChatData("2", "Bob_uid", "Bob", "What's up?", "11:30 AM")
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(sampleChats) { chat ->
            ChatListItem(chat = chat) {
                // Navigate to chat screen with proper arguments
                navController.navigate("chat/${chat.id}/${chat.receiverId}/${chat.name}")
            }
            HorizontalDivider()
        }
    }
}

@Composable
fun ChatListItem(chat: ChatData, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile picture
        if (!chat.profileUrl.isNullOrBlank()) {
            AsyncImage(
                model = chat.profileUrl,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(chat.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(chat.lastMessage, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Text(chat.time, style = MaterialTheme.typography.bodySmall)
    }
}
