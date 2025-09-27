package com.practice.whatsappclone

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun CallsListScreen(
    navController: NavController,
    callViewModel: CallViewModel = viewModel()
) {
    val calls = callViewModel.calls
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(calls) { isLoading = false }

    LaunchedEffect(callViewModel.error.value) {
        error = callViewModel.error.value
        callViewModel.error.value = null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("callsListContainer")
    ) {
        when {
            isLoading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF128C7E),
                        modifier = Modifier.testTag("callsLoadingIndicator")
                    )
                }
            }
            calls.isEmpty() -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No call history available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        modifier = Modifier.testTag("emptyCallsText")
                    )
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.testTag("callsList")) {
                    items(calls) { call ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp, 6.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        callViewModel.initiateCall(call.calleeUid, call.contactName, call.callId)
                                        val intent = Intent(context, CallActivity::class.java).apply {
                                            putExtra("callId", call.callId)
                                            putExtra("contactName", call.contactName)
                                            putExtra("callType", call.type)
                                            putExtra("launchedFromChat", false)
                                        }
                                        context.startActivity(intent)
                                    }
                                }
                                .testTag("callItem_${call.callId}"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Call,
                                    contentDescription = "Call Icon",
                                    modifier = Modifier.size(40.dp).testTag("callIcon_${call.callId}"),
                                    tint = Color(0xFF128C7E)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        call.contactName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.Black,
                                        modifier = Modifier.testTag("callName_${call.callId}")
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = call.type.replaceFirstChar { it.uppercase() } + " • " + formatTimestamp(call.time),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.DarkGray,
                                        modifier = Modifier.testTag("callTypeTime_${call.callId}")
                                    )
                                    Text(
                                        text = call.status.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = when (call.status.lowercase()) {
                                            "missed" -> Color.Red
                                            "accepted" -> Color.Green
                                            "ringing" -> Color(0xFFFFA500)
                                            "ended" -> Color.Gray
                                            else -> Color.DarkGray
                                        },
                                        modifier = Modifier.testTag("callStatus_${call.callId}")
                                    )
                                    if (call.duration > 0) {
                                        Text(
                                            text = "Duration: ${formatDuration(call.duration)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.DarkGray,
                                            modifier = Modifier.testTag("callDuration_${call.callId}")
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        error?.let {
            Snackbar(
                action = {
                    TextButton(onClick = { error = null }) {
                        Text("Dismiss")
                    }
                },
                modifier = Modifier.testTag("callsErrorSnackbar")
            ) {
                Text(text = it)
            }
        }
    }
}

fun formatTimestamp(time: Long): String {
    return android.text.format.DateFormat.format("dd MMM yyyy, hh:mm a", java.util.Date(time)).toString()
}

fun formatDuration(duration: Long): String {
    val seconds = (duration / 1000) % 60
    val minutes = (duration / (1000 * 60)) % 60
    val hours = (duration / (1000 * 60 * 60))
    return if (hours > 0)
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    else
        String.format("%02d:%02d", minutes, seconds)
}
