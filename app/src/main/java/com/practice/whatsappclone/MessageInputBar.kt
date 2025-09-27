package com.practice.whatsappclone

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun MessageInputBar(
    modifier: Modifier = Modifier,
    onSendMessage: (content: String, mediaUrl: String?, type: String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var pickedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePreview by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(GetContent()) { uri ->
        if (uri != null) {
            pickedImageUri = uri
            showImagePreview = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .background(Color.White)
            .testTag("messageInputBar") // TestTag for the input container
    ) {
        if (showImagePreview && pickedImageUri != null) {
            Box(
                Modifier
                    .padding(8.dp)
                    .size(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
                    .testTag("imagePreviewBox"), // TestTag for image preview box
                contentAlignment = Alignment.TopEnd
            ) {
                AsyncImage(
                    model = pickedImageUri,
                    contentDescription = "Picked Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("pickedImage") // TestTag for picked image
                )
                IconButton(
                    onClick = {
                        pickedImageUri = null
                        showImagePreview = false
                    },
                    modifier = Modifier.testTag("removeImageBtn") // TestTag for remove image button
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Remove Image", tint = Color.White)
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF0F0F0))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            IconButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.testTag("pickImageBtn") // TestTag for image picker button
            ) {
                Icon(Icons.Filled.Photo, contentDescription = "Pick Image", tint = Color.Gray)
            }

            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Type a message") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("messageInputField"), // TestTag for text input field
                maxLines = 4,
                enabled = !isSending,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            val canSend = text.isNotBlank() || pickedImageUri != null
            IconButton(
                onClick = {
                    if (pickedImageUri != null) {
                        onSendMessage("", pickedImageUri.toString(), "image")
                        pickedImageUri = null
                        showImagePreview = false
                    } else {
                        onSendMessage(text.trim(), null, "text")
                        text = ""
                    }
                },
                enabled = canSend,
                modifier = Modifier.testTag("sendMessageBtn") // TestTag for send button
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (canSend) Color(0xFF075E54) else Color.Gray
                )
            }
        }
    }
}
