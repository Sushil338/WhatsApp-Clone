package com.practice.whatsappclone

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class CallScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun callScreen_elementsVisible() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            // Disable loading during tests
            CallScreen(navController = navController, contactName = "Alice", simulateLoading = false)
        }

        composeTestRule.onNodeWithTag("contactNameText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("endCallBtn").assertIsDisplayed()
    }

    @Test
    fun callScreen_endCallButton_clickable() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CallScreen(navController = navController, contactName = "Alice", simulateLoading = false)
        }

        composeTestRule.onNodeWithTag("endCallBtn").performClick()
    }
}
