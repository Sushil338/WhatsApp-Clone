package com.practice.whatsappclone


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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CallsListScreen(
    navController: NavController,
    callViewModel: CallViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val callLogs: List<Call> = callViewModel.calls.toList()
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Simulate loading complete
    LaunchedEffect(callLogs) {
        isLoading = false
    }

    // Handle error
    LaunchedEffect(callViewModel.error.value) {
        error = callViewModel.error.value
        callViewModel.error.value = null
    }

    // Main content
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF128C7E))
                }
            }

            callLogs.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No call history available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 0.dp) // align right under tabs
                ) {
                    items(callLogs) { call ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .clickable {
                                    navController.navigate("call/${call.contactName}")
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE0F7FA)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Call,
                                    contentDescription = "Call",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color(0xFF128C7E)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = call.contactName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${call.type} • ${call.time}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Show error if exists
        ErrorSnackbar(error = error) { error = null }
    }
}
