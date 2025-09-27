package com.practice.whatsappclone

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.junit.Rule
import org.junit.Test

class WhatsAppTabRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tabRow_displaysAllTabs() {
        composeTestRule.setContent {
            var selectedTab by remember { mutableStateOf(0) }
            WhatsAppTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier
            )
        }

        composeTestRule.onNodeWithTag("tab_Chats").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tab_Status").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tab_Calls").assertIsDisplayed()
    }

    @Test
    fun tabRow_clickUpdatesSelectedTab() {
        composeTestRule.setContent {
            var selectedTab by remember { mutableStateOf(0) }
            WhatsAppTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier
            )
        }

        composeTestRule.onNodeWithTag("tab_Status").performClick()
        composeTestRule.onNodeWithTag("tab_Calls").performClick()
    }
}
