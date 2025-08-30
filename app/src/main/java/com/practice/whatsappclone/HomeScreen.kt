package com.practice.whatsappclone

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { WhatsAppAppBar() },
        floatingActionButton = { /* Add FloatingActionButton here if needed */ }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            WhatsAppTabRow(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            when (selectedTab) {
                0 -> ChatListScreen(onChatClicked = { chatId , receiverId ->
                    navController.navigate("chat/$chatId/$receiverId")
                })
//                1 -> StatusListScreen(navController)
//                2 -> CallsListScreen(navController)
            }
        }
    }
}
