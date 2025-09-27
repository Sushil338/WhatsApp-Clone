package com.practice.whatsappclone

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_initialUI_elementsVisible() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LoginScreen(
                navController = navController,
                viewModel = FakeAuthViewModel() // uses fake for isolated tests
            )
        }

        // Check phone input and country code input visible on the initial screen
        composeTestRule.onNodeWithTag("countryCodeInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("phoneInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nextBtn").assertIsDisplayed()

        // OTP input should not be visible initially
        composeTestRule.onNodeWithTag("otpInput").assertDoesNotExist()
        composeTestRule.onNodeWithTag("verifyBtn").assertDoesNotExist()
    }

    @Test
    fun loginScreen_fullFlow_phoneToOtp() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LoginScreen(
                navController = navController,
                viewModel = FakeAuthViewModel()
            )
        }

        // Enter phone number and press next
        composeTestRule.onNodeWithTag("phoneInput").performTextInput("1234567890")
        composeTestRule.onNodeWithTag("nextBtn").performClick()

        // OTP inputs become visible
        composeTestRule.onNodeWithTag("otpInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("verifyBtn").assertIsDisplayed()

        // Enter OTP and verify
        composeTestRule.onNodeWithTag("otpInput").performTextInput("123456")
        composeTestRule.onNodeWithTag("verifyBtn").performClick()
    }
}
