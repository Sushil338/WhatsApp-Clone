package com.practice.whatsappclone

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class CallsListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun callsList_UIElementsVisible() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            TestNavHost(navController = navController, startDestination = "calls_list")
        }

        composeTestRule.onNodeWithTag("callsListContainer").assertIsDisplayed()
        composeTestRule.onNodeWithTag("callsList").assertExists()
    }

    @Test
    fun callsList_clickCallItem() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            TestNavHost(navController = navController, startDestination = "calls_list")
        }

        composeTestRule.onNodeWithTag("callItem_sampleId").performClick()
    }
}
