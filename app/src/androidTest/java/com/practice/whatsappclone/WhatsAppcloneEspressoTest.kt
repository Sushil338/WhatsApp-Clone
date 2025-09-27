package com.practice.whatsappclone

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class WhatsAppCloneComposeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun sendTextMessageTest() {
        val messageText = "Hello from Compose Test"

        // Type message and send
        composeTestRule.onNodeWithTag("messageInputField").performTextInput(messageText)
        composeTestRule.onNodeWithTag("sendMessageBtn").performClick()

        // Verify message displayed
        composeTestRule.onNodeWithText(messageText).assertIsDisplayed()
    }

    @Test
    fun openChatFromListTest() {
        composeTestRule.onNodeWithTag("chatList").assertExists()
        composeTestRule.onNodeWithTag("chatItem_2").performClick()
        composeTestRule.onNodeWithTag("chatTopBar").assertIsDisplayed()
    }

    @Test
    fun scrollChatListTest() {
        composeTestRule.onNodeWithTag("chatList").performScrollToNode(hasText("Bob"))
        composeTestRule.onNodeWithText("Bob").assertIsDisplayed()
    }

    @Test
    fun openProfileScreenTest() {
        composeTestRule.onNodeWithTag("profileButton").performClick()
        composeTestRule.onNodeWithTag("profileName").assertIsDisplayed()
    }

    @Test
    fun openCallScreenTest() {
        composeTestRule.onNodeWithTag("callButton").performClick()
        composeTestRule.onNodeWithTag("callScreen").assertIsDisplayed()
    }
}
