package com.practice.whatsappclone

import android.net.Uri
import android.widget.Space
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

@Composable
fun UserProfileSetupScreen(navController: NavController, viewModel: ProfileViewModel, userId: String){

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        viewModel.photoUri = uri
    }

    Column (
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Set Up your Profile", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(32.dp))

        // Area for Profile picture
        Box(Modifier.size(96.dp).clip(CircleShape).background(Color.Gray)) {
            viewModel.photoUri?.let {
                Image(painter = rememberImagePainter(it), contentDescription = null, modifier = Modifier.fillMaxSize())
            } ?: Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.fillMaxSize())
        }
        TextButton(onClick = { launcher.launch("image/*") }) { Text("Pick Image") }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            label = { Text("Your Name") }
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { viewModel.saveUserProfile(userId) { navController.navigate("home") } },
            enabled = viewModel.name.isNotBlank() && !viewModel.isLoading
        ) { Text("Save Profile") }
        if (viewModel.isLoading) CircularProgressIndicator()
        viewModel.error?.let { Text(it, color = Color.Red) }
    }
}
