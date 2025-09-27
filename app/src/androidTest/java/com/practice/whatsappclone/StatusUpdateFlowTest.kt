package com.practice.whatsappclone

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class StatusUpdateFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun statusListScreen_showsStatuses() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            // Using real or fake StatusRepository as applicable
            StatusListScreen(navController)
        }

        // Wait for loading to finish if applicable
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            try {
                composeTestRule.onAllNodesWithTag("statusItem").fetchSemanticsNodes().isNotEmpty()
            } catch (e: Exception) {
                false
            }
        }

        // Verify at least one status item visible
        composeTestRule.onAllNodesWithTag("statusItem").onFirst().assertIsDisplayed()
    }

    @Test
    fun createStatusScreen_uiFlow() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            CreateStatusScreen(
                navController = navController,
                userId = "testUser",
                userName = "Test User",
                userProfileUrl = ""
            )
        }

        composeTestRule.onNodeWithText("Pick Image").assertIsDisplayed()
        composeTestRule.onNodeWithText("Upload Status").assertIsDisplayed()
    }
}
