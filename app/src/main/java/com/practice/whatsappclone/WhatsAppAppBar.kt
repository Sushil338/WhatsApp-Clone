package com.practice.whatsappclone

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsAppAppBar(){
    TopAppBar(
        title = {Text("WhatsApp Clone")},
        actions = {
            IconButton(onClick = {}) { Icon(Icons.Default.Search, contentDescription = "Search") }
            IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, contentDescription = "Search") }
        }
    )
}