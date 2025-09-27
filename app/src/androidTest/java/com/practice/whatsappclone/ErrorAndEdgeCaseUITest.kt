package com.practice.whatsappclone

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

@Composable
fun ErrorSnackbarTest(errorMessage: String?, onDismiss: () -> Unit) {
    if (!errorMessage.isNullOrEmpty()) {
        androidx.compose.material3.Snackbar(
            action = {
                androidx.compose.material3.Button(onClick = onDismiss) { Text("Dismiss") }
            },
            modifier = Modifier.testTag("errorSnackbar")
        ) {
            Text(errorMessage)
        }
    }
}

class ErrorAndEdgeCaseUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun errorSnackbar_displaysAndDismisses() {
        var showError by androidx.compose.runtime.mutableStateOf(true)
        val errorMsg = "This is an error"

        composeTestRule.setContent {
            ErrorSnackbarTest(errorMessage = if (showError) errorMsg else null) {
                showError = false
            }
        }

        composeTestRule.onNodeWithTag("errorSnackbar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dismiss").performClick()
        composeTestRule.waitUntil {
            try {
                composeTestRule.onAllNodesWithTag("errorSnackbar").fetchSemanticsNodes().isEmpty()
            } catch (e: AssertionError) {
                true // Snackbar gone
            }
        }
    }

    @Test
    fun loginScreen_invalidPhoneNumber_showsError() {
        val fakeAuthViewModel = FakeAuthViewModel().apply {
            errorMsg = "Invalid phone number"
        }

        composeTestRule.setContent {
            val navController = androidx.navigation.compose.rememberNavController()
            LoginScreen(navController, fakeAuthViewModel)
        }

        composeTestRule.onNodeWithText("Invalid phone number").assertIsDisplayed()
    }
}
