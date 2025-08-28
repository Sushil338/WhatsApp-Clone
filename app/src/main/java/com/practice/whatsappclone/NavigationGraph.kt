package com.practice.whatsappclone

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.*

@Composable
fun WhatsAppNavGraph(navController : NavHostController, authViewModel : AuthViewModel){
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash"){ SplashScreen(navController, authViewModel) }
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("home") { HomeScreen(navController) }

        composable("chat/{chatId}", arguments = listOf(navArgument("chatId") {type = NavType.StringType })
        ) { backStackEntry ->
            ChatScreen(chatId = backStackEntry.arguments?.getString("chatId") ?: "")
        }
    }
}