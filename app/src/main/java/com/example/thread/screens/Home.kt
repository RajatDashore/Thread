@file:Suppress("DEPRECATION")

package com.example.thread.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.thread.itemView.ThreadItem
import com.example.thread.viewModel.HomeViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Home(navHostController: NavHostController) {
    LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()
    val userThreads by homeViewModel.userThreads.collectAsState()
    val isLoading: State<Boolean> = homeViewModel.isLoading.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val notificationPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.POST_NOTIFICATIONS
    )

    if (openDialog.value) {
        RequestNotificationPermissionDialog(
            openDialog = openDialog,
            permissionState = notificationPermissionState
        )
    }


    LaunchedEffect(key1 = Unit) {
        if (notificationPermissionState.status.isGranted || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Firebase.messaging.subscribeToTopic("Tutorial")
        } else {
            openDialog.value = true
        }
    }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = 0,
        initialFirstVisibleItemScrollOffset = 0
    )

    // Fetch data on launch
    LaunchedEffect(userThreads.isEmpty()) {
        if (userThreads.isEmpty()) {
            homeViewModel.fetchUsersAndThreads()
        }
    }


    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading.value)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Thread",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC)) // Purple → Blue
                )
            ),
            modifier = Modifier.padding(10.dp)
        )

        HorizontalDivider(
            modifier = Modifier.height(1.dp),
            thickness = 1.dp,
            color = DividerDefaults.color
        )

        SwipeRefresh(
            state = swipeRefreshState, onRefresh = {
                homeViewModel.fetchUsersAndThreads()
            }) {
            if (isLoading.value && userThreads.isEmpty()) {
                LazyColumn {
                    items(5) { SkeletonThreadItem() }
                }
            } else {
                LazyColumn(state = listState) {
                    userThreads.forEach { userWithThreads ->
                        items(
                            userWithThreads.threads,
                            key = { thread -> thread.threadId!! }
                        ) { thread ->
                            ThreadItem(
                                thread = thread,
                                users = userWithThreads.user,
                                navHostController = navHostController,
                                userId = userWithThreads.user.uid ?: ""
                            )
                        }
                    }
                }

            }
        }
    }


}
@Composable
fun SkeletonThreadItem() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        // 🔹 Profile row
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Profile image placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .placeholder(
                        visible = true,
                        color = Color.LightGray,
                        shape = CircleShape,
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = Color.White.copy(alpha = 0.6f)
                        )
                    )

            )
            Spacer(modifier = Modifier.width(12.dp))

            // Username placeholder
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .width(120.dp)
                    .placeholder(
                        visible = true,
                        color = Color.LightGray
                    )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Date placeholder
            Box(
                modifier = Modifier
                    .height(14.dp)
                    .width(60.dp)
                    .placeholder(
                        visible = true,
                        color = Color.LightGray,
                        shape = CircleShape,
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = Color.White.copy(alpha = 0.6f)
                        )
                    )

            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Thread text placeholder
        Box(
            modifier = Modifier
                .height(18.dp)
                .fillMaxWidth(0.9f)
                .placeholder(
                    visible = true,
                    color = Color.LightGray,
                    shape = CircleShape,
                    highlight = PlaceholderHighlight.shimmer(
                        highlightColor = Color.White.copy(alpha = 0.6f)
                    )
                )

        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Optional thread image placeholder (full width, like final UI)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .placeholder(
                    visible = true,
                    color = Color.LightGray,
                    highlight = PlaceholderHighlight.shimmer(
                        highlightColor = Color.White.copy(alpha = 0.6f)
                    )
                )

        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Like row placeholder
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .placeholder(
                        visible = true,
                        color = Color.LightGray,
                        shape = CircleShape,
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = Color.White.copy(alpha = 0.6f)
                        )
                    )

            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .height(12.dp)
                    .width(40.dp)
                    .placeholder(
                        visible = true,
                        color = Color.LightGray,
                        shape = CircleShape,
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = Color.White.copy(alpha = 0.6f)
                        )
                    )

            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
    }
}
