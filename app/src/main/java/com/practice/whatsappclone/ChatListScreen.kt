package com.practice.whatsappclone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.testTag
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("chatList")
    ) {
        items(sampleChats) { chat ->
            ChatListItem(
                chat = chat,
                modifier = Modifier.testTag("chatItem_${chat.id}")
            ) {
                // Navigate to Chat screen with necessary params
                navController.navigate("chat/${chat.id}/${chat.receiverId}/${chat.name}")
            }
            Divider()
        }
    }
}

@Composable
fun ChatListItem(
    chat: ChatData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!chat.profileUrl.isNullOrBlank()) {
            AsyncImage(
                model = chat.profileUrl,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .testTag("profile_${chat.id}")
            )
        } else {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(60.dp)
                    .testTag("profile_${chat.id}")
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                chat.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.testTag("chatName_${chat.id}")
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                chat.lastMessage,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("lastMessage_${chat.id}")
            )
        }

        Text(
            chat.time,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.testTag("chatTime_${chat.id}")
        )
    }
}
