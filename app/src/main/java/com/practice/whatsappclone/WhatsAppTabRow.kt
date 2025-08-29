package com.practice.whatsappclone

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun WhatsAppTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val labels = listOf("Chats", "Status", "Calls")
    TabRow(selectedTabIndex = selectedTab) {
        labels.forEachIndexed { index, label ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) }
            ) {
                Text(label)
            }
        }
    }
}
