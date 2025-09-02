package com.practice.whatsappclone

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter


@Composable
fun StatusViewerScreen(
    navController: NavController,
    statusId: String,
    repository: StatusRepository = StatusRepository()
) {
    var status by remember { mutableStateOf<Status?>(null) }

    LaunchedEffect(statusId) {
        // Fetch the specific status from Firestore
        repository.getStatusById(statusId) { s -> status = s }
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        status?.let {
            Image(
                painter = rememberAsyncImagePainter(it.imageUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
        }
    }
}
