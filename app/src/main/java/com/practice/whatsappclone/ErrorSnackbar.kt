package com.practice.whatsappclone

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorSnackbar(error: String?, onDismiss: () -> Unit) {
    if (!error.isNullOrEmpty()) {  // Null and empty check
        Snackbar(
            action = { Button(onClick = onDismiss) { Text("Dismiss") } },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(error)
        }
    }
}
