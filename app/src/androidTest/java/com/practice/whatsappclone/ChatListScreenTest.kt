package com.practice.whatsappclone

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class ChatListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun chatList_displayItems() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            TestNavHost(navController = navController, startDestination = "home")
        }

        composeTestRule.onNodeWithTag("chatItem_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("chatItem_2").assertIsDisplayed()
    }

    @Test
    fun chatList_clickItem() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            TestNavHost(navController = navController, startDestination = "home")
        }

        composeTestRule.onNodeWithTag("chatItem_1").performClick()
    }
}
