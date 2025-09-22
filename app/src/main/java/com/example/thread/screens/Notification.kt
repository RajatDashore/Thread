package com.example.thread.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thread.itemView.NotificationItem
import com.example.thread.viewModel.NotificationViewModel
@Composable
fun Notification() {
    val notificationViewModel: NotificationViewModel = viewModel()
    val notificationList by notificationViewModel.notificationList.collectAsState()

    LaunchedEffect(Unit) {
        notificationViewModel.getAllNotifications()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 16.dp, start = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Screen Title
        Text(
            text = "Notifications",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)  // Align to start
        )

        if (notificationList.isEmpty()) {
            // Empty State
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No Notifications",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        } else {
            // Notifications List
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(notificationList) { not ->
                    NotificationItem(
                        not.image ?: "", not.notification ?: "No message", not.time?:"00:00"
                    )
                }
            }
        }
    }
}
