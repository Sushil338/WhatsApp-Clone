package com.practice.whatsappclone

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WhatsAppTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val labels = listOf("Chats", "Status", "Calls")

    TabRow(
        selectedTabIndex = selectedTab,
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        containerColor = Color(0xFF075E54),
        contentColor = Color.White,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTab])
                    .height(3.dp),
                color = Color.White
            )
        }
    ) {
        labels.forEachIndexed { index, label ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color(0xFFB2DFDB),
                modifier = Modifier.testTag("tab_$label")
            ) {
                Text(
                    text = label,
                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTab == index) Color.White else Color(0xFFB2DFDB),
                    fontSize = 16.sp
                )
            }
        }
    }
}
