package com.practice.whatsappclone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun TestNavHost(navController: NavHostController, startDestination: String = "login") {
    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            val fakeAuthViewModel = remember { FakeAuthViewModel() }
            LoginScreen(navController = navController, viewModel = fakeAuthViewModel)
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("profile/{userId}", arguments = listOf(
            navArgument("userId") { type = NavType.StringType }
        )) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val fakeProfileViewModel = remember { FakeProfileViewModel() }
            ProfileScreen(navController, fakeProfileViewModel, userId)
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

        composable("calls_list") {
            CallsListScreen(navController)
        }

        composable(
            "call/{contactName}",
            arguments = listOf(navArgument("contactName") { type = NavType.StringType })
        ) { backStackEntry ->
            val contactName = backStackEntry.arguments?.getString("contactName") ?: ""
            CallScreen(navController, contactName)
        }

        composable("settings") {
            SettingsScreen(navController)
        }

        composable("search") {
            SearchScreen(navController)
        }

        composable(
            "incoming_call/{callId}/{callerName}/{callType}",
            arguments = listOf(
                navArgument("callId") { type = NavType.StringType },
                navArgument("callerName") { type = NavType.StringType },
                navArgument("callType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val callId = backStackEntry.arguments?.getString("callId") ?: ""
            val callerName = backStackEntry.arguments?.getString("callerName") ?: ""
            val callType = backStackEntry.arguments?.getString("callType") ?: "video"
            IncomingCallScreen(
                callId = callId,
                callerName = callerName,
                callType = callType,
                onAccept = { /* No-op for tests */ },
                onDecline = { navController.popBackStack() }
            )
        }
    }
}
