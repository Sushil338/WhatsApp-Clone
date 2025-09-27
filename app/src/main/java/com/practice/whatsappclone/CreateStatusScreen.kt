package com.practice.whatsappclone

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
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
        }

        Button(
            onClick = {
                if (imageUri != null) {
                    loading = true
                    repository.postStatus(userId, userName, userProfileUrl, imageUri!!) { success, msg ->
                        loading = false
                        if (success) {
                            navController.popBackStack() // Return to previous screen after successful upload
                        } else {
                            error = msg
                        }
                    }
                } else {
                    error = "Please select an image first"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading && imageUri != null
        ) {
            Text("Upload Status")
        }

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
