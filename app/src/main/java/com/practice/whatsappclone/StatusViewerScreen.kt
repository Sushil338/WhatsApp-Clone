package com.practice.whatsappclone

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
    repository: StatusRepository = StatusRepository(),
    modifier: Modifier = Modifier
) {
    var status by remember { mutableStateOf<Status?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(statusId) {
        try {
            repository.getStatusById(statusId) { s ->
                status = s
                loading = false
            }
        } catch (e: Exception) {
            error = e.message ?: "Failed to load status"
            loading = false
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            status != null -> Image(
                painter = rememberAsyncImagePainter(status!!.imageUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            else -> Text(
                text = "Status not found",
                modifier = Modifier.align(Alignment.Center)
            )
        }

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
        }

        ErrorSnackbar(
            error = error,
            onDismiss = { error = null },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
