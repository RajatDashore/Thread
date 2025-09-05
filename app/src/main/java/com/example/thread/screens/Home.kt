package com.example.thread.screens

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.thread.itemView.ThreadItem
import com.example.thread.viewModel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Home(navHostController: NavHostController) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()
    val TAG = "SHOWIMAGE"
    val userThreads by homeViewModel.userThreads.observeAsState(emptyList())


    LaunchedEffect(Unit) {
        homeViewModel.fetchUsersAndThreads()
    }

    LazyColumn {
        userThreads.forEach { userWithThreads ->
            items(userWithThreads.threads) { thread ->
                ThreadItem(
                    thread = thread,
                    users = userWithThreads.user,
                    navHostController,
                    userId = userWithThreads.user.uid!!
                )

            }

        }
    }


}

