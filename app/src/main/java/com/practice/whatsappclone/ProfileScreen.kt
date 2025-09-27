package com.practice.whatsappclone

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    userId: String,
    onSaveNavigateTo: String = "home",
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        viewModel.photoUri = it
    }

    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val phoneNumber = firebaseUser?.phoneNumber ?: "Not Available"

    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(viewModel.error) {
        error = viewModel.error
        viewModel.error = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Set Up Your Profile",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF075E54),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF0F2F5),
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                // Profile picture
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFBDBDBD)),
                    contentAlignment = Alignment.Center
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
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.White
                    )
                }
                TextButton(
                    onClick = { launcher.launch("image/*") },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF128C7E))
                ) {
                    Text("Change Photo")
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Profile inputs
                ProfileTextField(
                    value = viewModel.name,
                    onValueChange = { viewModel.name = it },
                    label = "Your Name",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileTextField(
                    value = viewModel.about,
                    onValueChange = { viewModel.about = it },
                    label = "About",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileTextField(
                    value = viewModel.bio,
                    onValueChange = { viewModel.bio = it },
                    label = "Bio",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileTextField(
                    value = phoneNumber,
                    onValueChange = {},
                    label = "Phone Number",
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.saveUserProfile(userId) {
                            navController.navigate(onSaveNavigateTo) {
                                popUpTo("profile/$userId") { inclusive = true }
                            }
                        }
                    },
                    enabled = viewModel.name.isNotBlank() && !viewModel.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF075E54),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Profile", fontSize = 16.sp)
                }

                if (viewModel.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = Color(0xFF128C7E))
                }

                error?.let {
                    ErrorSnackbar(
                        error = it,
                        onDismiss = { error = null },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        singleLine = true,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF128C7E),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color(0xFF128C7E),
            cursorColor = Color(0xFF128C7E)
        )
    )
}
