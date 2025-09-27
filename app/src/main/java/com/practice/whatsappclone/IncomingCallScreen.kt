package com.practice.whatsappclone

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun IncomingCallScreen(
    callId: String,
    callerName: String,
    callType: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("incomingCallScreen"),
        color = Color(0xFF075E54)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Incoming $callType Call",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.testTag("incomingCallText")
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                callerName,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.testTag("callerNameText")
            )
            Spacer(modifier = Modifier.height(48.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
                Button(
                    onClick = onDecline,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .size(100.dp)
                        .testTag("declineCallBtn"),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "Decline Call", tint = Color.White)
                }
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                    modifier = Modifier
                        .size(100.dp)
                        .testTag("acceptCallBtn"),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Accept Call", tint = Color.White)
                }
            }
        }
    }
}
