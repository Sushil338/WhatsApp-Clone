package com.practice.whatsappclone

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, viewModel: AuthViewModel){
    LaunchedEffect(Unit) {
        delay(1500)
        if (viewModel.checkLogin()) navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
        else navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center){
        Text("WhatsApp Clone\n(Splash)", textAlign = TextAlign.Center)
    }
}