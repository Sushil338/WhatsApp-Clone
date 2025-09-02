package com.practice.whatsappclone

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.foundation.background
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    userId: String,
    onSaveNavigateTo: String = "home"
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModel.photoUri = uri
    }
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val phoneNumber = firebaseUser?.phoneNumber ?: "Not Available"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Up your Profile") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            ) {
                viewModel.photoUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            TextButton(onClick = { launcher.launch("image/*") }) { Text("Pick Image") }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = viewModel.about,
                onValueChange = { viewModel.about = it },
                label = { Text("About") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = viewModel.bio,
                onValueChange = { viewModel.bio = it },
                label = { Text("Bio") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {},
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { viewModel.saveUserProfile(userId) { navController.navigate(onSaveNavigateTo) } },
                enabled = viewModel.name.isNotBlank() && !viewModel.isLoading
            ) { Text("Save Profile") }
            if (viewModel.isLoading) CircularProgressIndicator()
            viewModel.error?.let { Text(it, color = Color.Red) }
        }
    }
}
