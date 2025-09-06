package com.example.thread.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.thread.itemView.ThreadItem
import com.example.thread.viewModel.HomeViewModel
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

@Composable
fun Home(navHostController: NavHostController) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()
    val userThreads by homeViewModel.userThreads.collectAsState()
    var isLoading = homeViewModel.isLoading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }

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
            LazyColumn {
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
