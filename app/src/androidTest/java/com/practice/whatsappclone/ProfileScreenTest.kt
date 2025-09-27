package com.practice.whatsappclone

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun profileScreen_displaysUserInfo() {
        val fakeViewModel = FakeProfileViewModel()
        val user = fakeViewModel.getFakeUser("testUserId")

        composeTestRule.setContent {
            val navController = rememberNavController()
            ProfileScreen(
                navController = navController,
                viewModel = fakeViewModel,
                userId = "testUserId"
            )
        }

        // Assertions using dummy data
        composeTestRule.onNodeWithText(user["name"] as String).assertIsDisplayed()

        // Assuming "Edit" button or text exists for profile editing; replace as per your UI
        composeTestRule.onNodeWithText("Change Photo").assertIsDisplayed()
    }
}
