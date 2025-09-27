package com.practice.whatsappclone

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class ChatScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun chatScreen_UIElementsVisible() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ChatScreen(
                chatId = "1",
                receiverId = "Alice_uid",
                receiverName = "Alice",
                navController = navController
            )
        }

        composeTestRule.onNodeWithTag("chatTopBar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("messageInputBar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("chatScreenContainer").assertIsDisplayed()
    }
}
