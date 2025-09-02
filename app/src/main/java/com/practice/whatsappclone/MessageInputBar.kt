package com.practice.whatsappclone

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MessageInputBar(
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

    Column {
        if (showImagePreview && pickedImageUri != null) {
            Box(
                Modifier
                    .padding(8.dp)
                    .size(150.dp)
                    .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_report_image),
                    contentDescription = "Picked Image",
                    modifier = Modifier.fillMaxSize()
                )
                // Replace above painterResource with your image loader/painter such as Coil's rememberImagePainter(pickedImageUri)
                // Example with Coil:
                // Image(painter = rememberImagePainter(pickedImageUri), contentDescription = null, modifier = Modifier.fillMaxSize())
                // Add an 'X' button to remove image
                IconButton(
                    onClick = {
                        pickedImageUri = null
                        showImagePreview = false
                    },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Filled.AttachFile, contentDescription = "Remove Image")
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                Icon(Icons.Filled.Photo, contentDescription = "Pick Image")
            }

            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Type a message") },
                modifier = Modifier.weight(1f),
                maxLines = 4,
                enabled = !isSending
            )

            Spacer(modifier = Modifier.width(8.dp))

            val canSend = text.isNotBlank() || pickedImageUri != null

            IconButton(
                onClick = {
                    if (pickedImageUri != null) {
                        // When an image is picked, send it as image type with null content or empty content
                        onSendMessage("", pickedImageUri.toString(), "image")
                        pickedImageUri = null
                        showImagePreview = false
                    } else {
                        onSendMessage(text.trim(), null, "text")
                        text = ""
                    }
                },
                enabled = canSend
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}
