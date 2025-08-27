package com.practice.whatsappclone

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun WhatsAppTabRow(selectedTab: Int) {
    TabRow(selectedTabIndex = selectedTab) {
        Tab(selected = selectedTab == 0, onClick = { /*TODO*/}){
            Text("Chats")
        }
        Tab(selected = selectedTab == 1, onClick = { /*TODO*/}){
            Text("Status")
        }
        Tab(selected = selectedTab == 2, onClick = { /*TODO*/}){
            Text("Calls")
        }
    }
}
