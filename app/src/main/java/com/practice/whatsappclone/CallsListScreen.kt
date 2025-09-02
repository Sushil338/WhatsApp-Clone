package com.practice.whatsappclone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class CallLog(
    val id: String,
    val contactName: String,
    val time: String,
    val type: String // Missed, Incoming, Outgoing
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallsListScreen(navController: NavController){
    val callLogs = listOf(
        CallLog("1", "Alice", "Today, 11:15 AM", "Incoming"),
        CallLog("2", "Bob", "Yesterday, 8:30 PM", "Outgoing"),
        CallLog("3", "Charlie", "Yesterday, 3:41 PM", "Missed")
    )

    Scaffold { padding ->
        LazyColumn(Modifier.fillMaxSize()) {
            items(callLogs) { call ->
                Row(Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        navController.navigate("call/${call.contactName}")
                    })
                {
                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(40.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(call.contactName, style = MaterialTheme.typography.titleMedium)
                        Text(call.time, style = MaterialTheme.typography.bodySmall)
                        Text(call.type, style = MaterialTheme.typography.bodySmall)
                    }
                }
                HorizontalDivider()

            }
        }
    }
}