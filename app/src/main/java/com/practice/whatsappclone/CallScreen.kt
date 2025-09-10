package com.practice.whatsappclone


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CallScreen(
    navController: NavController,
    contactName: String,
    callViewModel: CallViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Sync ViewModel errors
    LaunchedEffect(callViewModel.error.value) {
        error = callViewModel.error.value
        callViewModel.error.value = null
    }

    // Simulate call setup
    LaunchedEffect(Unit) {
        isLoading = true
        kotlinx.coroutines.delay(800) // small delay for loading effect
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF075E54), Color(0xFF128C7E))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> CircularProgressIndicator(color = Color.White)
            else -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "In Call With",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = contactName,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(50.dp))
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(90.dp)
                            .background(Color.Red, shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "End Call",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Error Snackbar
        ErrorSnackbar(error = error) { error = null }
    }
}
