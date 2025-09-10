package com.practice.whatsappclone

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WhatsAppTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val labels = listOf("Chats", "Status", "Calls")

    TabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp), // taller like WhatsApp
        containerColor = Color(0xFF075E54), // use same green so it visually matches AppBar
        contentColor = Color.White,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTab])
                    .height(3.dp), // explicit indicator height
                color = Color.White
            )
        }
    ) {
        labels.forEachIndexed { index, label ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color(0xFFB2DFDB)
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
