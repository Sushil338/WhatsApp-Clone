package com.practice.whatsappclone

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>() // launcher activity

    @Test
    fun clickingChatItem_opensChatScreen() {
        // Check chat list exists
        composeTestRule.onNodeWithTag("chatList").assertExists()

        // Click chat item with tag chatItem_2 (Bob's chat)
        composeTestRule.onNodeWithTag("chatItem_2").performClick()

        // Verify ChatScreen UI loaded
        composeTestRule.onNodeWithTag("chatTopBar").assertExists()
        composeTestRule.onNodeWithTag("messageInputBar").assertExists()
    }
}
