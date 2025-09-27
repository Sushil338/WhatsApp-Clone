package com.practice.whatsappclone

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class FullAppFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fullAppFlow_loginToCall() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            TestNavHost(navController = navController, startDestination = "login")
        }

        // 1️⃣ Login flow
        composeTestRule.onNodeWithTag("phoneInput").performTextInput("1234567890")
        composeTestRule.onNodeWithTag("nextBtn").performClick()
        composeTestRule.onNodeWithTag("otpInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("otpInput").performTextInput("123456")
        composeTestRule.onNodeWithTag("verifyBtn").performClick()

        // 2️⃣ ChatList
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("chatItem_1").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("chatItem_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("chatItem_1").performClick()

        // 3️⃣ ChatScreen
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("chatTopBar").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("chatTopBar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("messageInputBar").assertIsDisplayed()

        // 4️⃣ Send message
        composeTestRule.onNodeWithTag("messageInputField").performTextInput("Hello!")
        composeTestRule.onNodeWithTag("sendMessageBtn").performClick()

        // 5️⃣ Start Call (audio)
        composeTestRule.onNodeWithTag("audioCallBtn").performClick()

        // 6️⃣ Verify CallScreen
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("contactNameText").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("contactNameText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("endCallBtn").performClick()
    }
}
