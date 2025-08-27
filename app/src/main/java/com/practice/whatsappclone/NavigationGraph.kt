package com.practice.whatsappclone

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.*

@Composable
fun WhatsAppNavGraph(navController : NavHostController){
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }

        composable("chat/{chatId}", arguments = listOf(navArgument("chatId") {type = NavType.StringType })
        ) { backStackEntry ->
            ChatScreen(chatId = backStackEntry.arguments?.getString("chatId") ?: "")
        }
    }
}