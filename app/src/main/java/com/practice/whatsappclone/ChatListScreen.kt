package com.practice.whatsappclone


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


data class ChatData(
    val id: String,
    val receiverId: String,
    val name : String,
    val lastMessage : String,
    val time : String
)

@Composable
fun ChatListScreen(onChatClicked: (String, String) -> Unit){
    val sampleChats = listOf(
        ChatData("1", "Alice_uid","Alice", "Hey there!", "10:00 AM"),
        ChatData("2", "BOB_uid","BOB", "what's up?", "11:30 AM")
    )
    LazyColumn {
        items(sampleChats) { chat ->
            ChatListItem(chat = chat, onClick = { onChatClicked(chat.id, chat.receiverId) })
            Divider()
        }
    }
}

@Composable
fun ChatListItem(chat : ChatData, onClick: () -> Unit){
    Row(
        modifier = Modifier.fillMaxWidth().clickable{onClick()}.padding(16.dp)
    ){
        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", modifier = Modifier.size(40.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(chat.name, fontWeight = FontWeight.Bold)
            Text(chat.lastMessage)
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(chat.time)
    }
}