package com.practice.whatsappclone

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

// Dummy composable simulating notification display for UI test
@Composable
fun NotificationScreen(message: String) {
    Text(text = message, modifier = androidx.compose.ui.Modifier.testTag("notificationMessage"))
}

class NotificationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun notificationMessage_displaysCorrectly() {
        val testMessage = "New Message Received"

        composeTestRule.setContent {
            NotificationScreen(message = testMessage)
        }

        composeTestRule.onNodeWithTag("notificationMessage")
            .assertIsDisplayed()
            .assertTextEquals(testMessage)
    }
}
