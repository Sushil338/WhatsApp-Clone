package com.practice.whatsappclone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
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
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(
                "Edit Profile",
                modifier = Modifier.fillMaxWidth().clickable {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    navController.navigate("profile/$userId")
                }.padding(16.dp)
            )
            HorizontalDivider()
            Text(
                "Notifications",
                modifier = Modifier.fillMaxWidth().clickable {
                    navController.navigate("notifications")
                }.padding(16.dp)
            )
            HorizontalDivider()
            Text(
                "Log Out",
                modifier = Modifier.fillMaxWidth().clickable {
                    // Firebase sign out
                    FirebaseAuth.getInstance().signOut()

                    // clear any app cached data or ViewModel states here if needed

                    // Navigate to login clearing backstack
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }.padding(16.dp)
            )
            HorizontalDivider()
        }
    }
}
