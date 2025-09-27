package com.practice.whatsappclone

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class IncomingCallScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun incomingCallScreen_elementsVisible() {
        composeTestRule.setContent {
            IncomingCallScreen(
                callId = "1",
                callerName = "Alice",
                callType = "Video",
                onAccept = {},
                onDecline = {}
            )
        }

        composeTestRule.onNodeWithTag("incomingCallText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("acceptCallBtn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("declineCallBtn").assertIsDisplayed()
    }
}
