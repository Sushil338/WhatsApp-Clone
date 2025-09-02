package com.practice.whatsappclone


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CallScreen(
    navController: NavController,
    contactName: String
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "In Call With", color = Color.White)
            Text(text = contactName, color = Color.White)
            Spacer(Modifier.height(40.dp))
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(80.dp).background(Color.Red, shape = androidx.compose.foundation.shape.CircleShape)
            ) {
                Icon(Icons.Default.CallEnd, "End Call", tint = Color.White)
            }
            Spacer(Modifier.height(16.dp))
            Text(text = "End Call", color = Color.White)
        }
    }
}
