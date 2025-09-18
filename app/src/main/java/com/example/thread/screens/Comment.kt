package com.example.thread.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.thread.R
import com.example.thread.itemView.CommentItem
import com.example.thread.viewModel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Comment(
    navHostController: NavHostController,
    otherUid: String,
    threadId: String,
    homeViewModel: HomeViewModel
) {
    val comments by homeViewModel.userComment.collectAsState()
    val keyBoardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(threadId) {
        homeViewModel.fetchComment(otherUid, threadId)
    }

    var commentText by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Comments") }) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Add a comment") }
                )

                IconButton(
                    onClick = {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (!commentText.isBlank() && currentUser != null) {
                            homeViewModel.addComment(
                                otherUid = otherUid,
                                currentUid = currentUser.uid,
                                threadId = threadId,
                                comment = commentText
                            )
                            commentText = ""
                            keyBoardController?.hide()
                        }
                    }
                ) {
                    Icon(painterResource(R.drawable.baseline_send_24), contentDescription = "Send")
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            items(comments) { commentWithUser ->
                CommentItem(commentWithUser)
            }
        }
    }
}
