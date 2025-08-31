package com.practice.whatsappclone

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WhatsAppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController, authViewModel)
        }

        composable("login") {
            LoginScreen(navController, authViewModel)
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("profile") { backStackEntry ->
            val profileViewModel: ProfileViewModel = viewModel()
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            UserProfileSetupScreen(navController, profileViewModel, userId)
        }

        composable(
            "chat/{chatId}/{receiverId}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("receiverId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            val receiverId = backStackEntry.arguments?.getString("receiverId") ?: ""
            ChatScreen(chatId = chatId, receiverId = receiverId)
        }

        composable("status_list"){ backStackEntry ->
            StatusListScreen(
                navController = navController,
                repository = StatusRepository()
            )
        }

        composable("status_viewer/{statusId}") { backStackEntry ->
            val statusId = backStackEntry.arguments?.getString("statusId") ?: ""
            StatusViewerScreen(navController, statusId)
        }

        composable("create_status") {
            val user = FirebaseAuth.getInstance().currentUser
            val userId = user?.uid ?: ""
            val userName = user?.displayName ?: "Unknown User"
            val userProfileUrl = user?.photoUrl?.toString() ?: ""

            CreateStatusScreen(
                navController = navController,
                repository = StatusRepository(),
                userId = userId,
                userName = userName,
                userProfileUrl = userProfileUrl
            )
        }


    }
}
