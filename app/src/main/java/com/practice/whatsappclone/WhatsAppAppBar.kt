package com.practice.whatsappclone

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsAppAppBar(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                "WhatsApp Clone",
                color = Color.White
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF075E54), // WhatsApp green
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        actions = {
            // Search button navigates to Search screen
            IconButton(onClick = {
                navController.navigate("search")
            }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }

            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Settings") },
                    onClick = {
                        expanded = false
                        navController.navigate("settings")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Profile") },
                    onClick = {
                        expanded = false
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        navController.navigate("profile/$userId")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = {
                        expanded = false
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                )
            }

            IconButton(onClick = {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                navController.navigate("profile/$userId")
            }) {
                Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
            }
        }
    )
}
