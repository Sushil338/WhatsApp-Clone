package com.practice.whatsappclone


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WhatsAppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {

        
        composable("login") { LoginScreen(navController, authViewModel) }

        composable("home") { HomeScreen(navController) }

        composable("profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val profileViewModel: ProfileViewModel = viewModel()
            ProfileScreen(navController, profileViewModel, userId)
        }

        composable(
            "chat/{chatId}/{receiverId}/{receiverName}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("receiverId") { type = NavType.StringType },
                navArgument("receiverName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            val receiverId = backStackEntry.arguments?.getString("receiverId") ?: ""
            val receiverName = backStackEntry.arguments?.getString("receiverName") ?: ""
            ChatScreen(chatId, receiverId, receiverName, navController)
        }

        composable("calls_list") { CallsListScreen(navController) }

        composable(
            "call/{contactName}",
            arguments = listOf(navArgument("contactName") { type = NavType.StringType })
        ) { backStackEntry ->
            val contactName = backStackEntry.arguments?.getString("contactName") ?: ""
            CallScreen(navController, contactName)
        }

        composable("settings") { SettingsScreen(navController) }


        composable("search") {
            SearchScreen(navController)
        }
    }
}
