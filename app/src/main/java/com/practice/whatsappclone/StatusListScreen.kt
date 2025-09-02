package com.practice.whatsappclone

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatusListScreen(
    navController: NavController,
    repository: StatusRepository = StatusRepository()
){
    var statuses by remember { mutableStateOf<List<Status>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        repository.listenToStatuses {
            statuses = it
            loading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()){

        if(loading){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        else if(statuses.isEmpty()){
            Text(text = "No Status Found", modifier = Modifier.align(Alignment.Center))
        }
        else{
            LazyColumn {
                items(statuses) { status ->
                    Row(
                        modifier = Modifier.fillMaxSize()
                            .clickable {navController.navigate("status_viewer/${status.statusId}")}
                            .padding((12.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberImagePainter(status.imageUrl),
                            contentDescription = null,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = status.userName)
                            val formattedTime = remember(status.timeStamp) {
                                formatTimeAgo(status.timeStamp)
                            }
                            Text(text = formattedTime)
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("create_status") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Status")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTimeAgo(timeMillis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timeMillis

    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24 -> "$hours hours ago"
        days < 7 -> "$days days ago"
        else -> {
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
            Instant.ofEpochMilli(timeMillis).atZone(ZoneId.systemDefault())
                .format(formatter)
        }
    }
}