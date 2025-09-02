package com.practice.whatsappclone

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@Composable
fun CreateStatusScreen(
    navController: NavController,
    repository: StatusRepository = StatusRepository(),
    userId: String,
    userName: String,
    userProfileUrl: String
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
        error = null
    }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        if (loading) CircularProgressIndicator()
        Button(onClick = { launcher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text("Pick Image")
        }
        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 16.dp)
            )
            Button(
                onClick = {
                    loading = true
                    repository.postStatus(userId, userName, userProfileUrl, it) { success, msg ->
                        loading = false
                        if (success) navController.popBackStack()
                        else error = msg
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Upload Status") }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}
