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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Home(navHostController: NavHostController) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()
    val userThreads by homeViewModel.userThreads.collectAsState()
    var isLoading = homeViewModel.isLoading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
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

    var listState = rememberLazyListState(
        initialFirstVisibleItemIndex = 0,
        initialFirstVisibleItemScrollOffset = 0
    )

    // Fetch data on launch
    LaunchedEffect(userThreads.isEmpty()) {
        if (userThreads.isEmpty()) {
            homeViewModel.fetchUsersAndThreads()
        }
    }

    // Update loading state
    LaunchedEffect(userThreads) {
        if (isLoading.value) isRefreshing = false
    }

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

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
                isRefreshing = true
                homeViewModel.fetchUsersAndThreads()
            }) {
            if (isLoading.value) {
                // 🔹 Show shimmer placeholders while loading
                LazyColumn {
                    items(5) { SkeletonThreadItem() }
                }
            } else {
                isRefreshing = false
                // 🔹 Show real feed
                LazyColumn(state = listState) {
                    items(userThreads, key = { it.user.uid ?: "" }) { userWithThreads ->
                        userWithThreads.threads.forEach { thread ->
                            ThreadItem(
                                thread,
                                userWithThreads.user,
                                navHostController,
                                userWithThreads.user.uid!!
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
            .padding(16.dp)
    ) {
        // Fake profile row
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .placeholder(
                        visible = true, color = Color.LightGray,
                        // highlight = shimmer(),
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .width(120.dp)
                    .placeholder(
                        visible = true,
                        color = Color.LightGray,
                        //   highlight = shimmer()
                    )
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Fake text
        Box(
            modifier = Modifier
                .height(18.dp)
                .fillMaxWidth(0.9f)
                .placeholder(
                    visible = true,
                    color = Color.LightGray,
                    // highlight = shimmer()
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
                .placeholder(
                    visible = true,
                    color = Color.LightGray,
                    //  highlight = shimmer()
                )
        )
    }
}

