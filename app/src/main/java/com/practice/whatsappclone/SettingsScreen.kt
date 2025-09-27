package com.practice.whatsappclone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val settingsViewModel: SettingsViewModel = viewModel()
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(settingsViewModel.error.value) {
        error = settingsViewModel.error.value
        settingsViewModel.error.value = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = modifier.padding(16.dp)) {

                Text(
                    "Edit Profile",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            navController.navigate("profile/$userId")
                        }
                        .padding(16.dp)
                )
                Divider(modifier = Modifier.fillMaxWidth())

                Text(
                    "Notifications",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("notifications") }
                        .padding(16.dp)
                )
                Divider(modifier = Modifier.fillMaxWidth())

                Text(
                    "Log Out",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            settingsViewModel.performLogout {
                                navController.navigate("login") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
                        }
                        .padding(16.dp)
                )
                Divider(modifier = Modifier.fillMaxWidth())
            }

            error?.let {
                ErrorSnackbar(
                    error = it,
                    onDismiss = { error = null },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
            }
        }
    }
}
