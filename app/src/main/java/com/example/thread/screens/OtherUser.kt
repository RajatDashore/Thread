package com.example.thread.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.example.thread.R
import com.example.thread.itemView.ThreadItem
import com.example.thread.navigation.Routes
import com.example.thread.utils.SharedPref
import com.example.thread.viewModel.AuthViewModel
import com.example.thread.viewModel.OtherUserViewModel
import com.example.thread.viewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun OtherUsers(navHostController: NavHostController, uid: String?) {
    val userViewModel: UserViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val threads by userViewModel.threads.observeAsState(null)
    val users by userViewModel.users.observeAsState(null)
    val firebaseUser by authViewModel.firebaseUser.observeAsState(null)
    val otherUserViewMode: OtherUserViewModel = viewModel()
    val context = LocalContext.current
    val otherUserViewModel: OtherUserViewModel = viewModel()
    val follower by otherUserViewModel.followerCount.observeAsState(0)
    val follewing by otherUserViewModel.followingCount.observeAsState(0)
    val isFollowing by otherUserViewModel.isFollowing.observeAsState(false)





    LaunchedEffect(firebaseUser) {
        if (firebaseUser == null || uid == null) {
            navHostController.navigate(Routes.Login.route) {
                navHostController.navigate(Routes.Login.route) {
                    popUpTo(navHostController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        } else {
            userViewModel.fetchThread(uid!!)
            userViewModel.fetchUser(uid!!)
            otherUserViewModel.fetchFollowersAndFollowingCounts(uid)
            otherUserViewModel.checkUserStatus(uid)


        }
    }





    LazyColumn {
        item {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                val (text, logo, userName, bio, followers, following, LogButton) = createRefs()


                Text(
                    text = users?.name ?: "Unknown User", style = TextStyle(
                        fontWeight = FontWeight.Bold, fontSize = 24.sp
                    ), modifier = Modifier.constrainAs(text) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    })
                val painter = if (users?.imageUri != null) {
                    rememberAsyncImagePainter(users?.imageUri)
                } else {
                    painterResource(id = R.drawable.baseline_person_24)
                }
                Image(
                    painter = painter,
                    contentDescription = "Logo",
                    modifier = Modifier
                        .constrainAs(logo) {
                            top.linkTo(text.bottom)
                            end.linkTo(parent.end)
                        }
                        .size(96.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop)

                Text(
                    text = users?.username ?: "Unknown UserName", style = TextStyle(
                        fontSize = 20.sp
                    ), modifier = Modifier.constrainAs(userName) {
                        top.linkTo(text.bottom)
                        start.linkTo(parent.start)
                    })

                Text(
                    text = users?.bio ?: "Unknown Bio", style = TextStyle(
                        fontSize = 20.sp
                    ), modifier = Modifier.constrainAs(bio) {
                        top.linkTo(userName.bottom)
                        start.linkTo(parent.start)
                    })

                Text(
                    text = "$follower followers", style = TextStyle(
                        fontSize = 20.sp
                    ), modifier = Modifier.constrainAs(followers) {
                        top.linkTo(bio.bottom)
                        start.linkTo(parent.start)
                    })
                Text(
                    text = "$follewing following", style = TextStyle(
                        fontSize = 20.sp
                    ), modifier = Modifier.constrainAs(following) {
                        top.linkTo(followers.bottom)
                        start.linkTo(parent.start)

                    })


                Column(modifier = Modifier.constrainAs(LogButton) {
                    top.linkTo(following.bottom)
                    start.linkTo(parent.start)
                }) {
                    Box(modifier = Modifier.height(8.dp)) {}

                    ElevatedButton(
                        onClick = {
                            if (FirebaseAuth.getInstance().currentUser!!.uid == uid) {
                                authViewModel.logout()
                            } else {
                                otherUserViewModel.doFollow(
                                    uid!!,
                                    users!!.username.toString(),
                                    System.currentTimeMillis().toString(),
                                    context
                                )
                                otherUserViewModel.checkUserStatus(uid) // update follow status after action
                            }
                        },
                        modifier = Modifier.padding(top = 5.dp)
                    ) {
                        when {
                            FirebaseAuth.getInstance().currentUser!!.uid == uid -> Text(
                                "Logout",
                                color = Color.Blue
                            )

                            isFollowing -> Text("Unfollow", color = Color.Blue)
                            else -> Text("Follow", color = Color.Blue)
                        }
                    }


                    Box(modifier = Modifier.height(8.dp))


                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(Color.LightGray)
                    ) {}
                }


            }
        }


        if (threads != null && users != null) {
            items(threads ?: emptyList()) { pair ->
                ThreadItem(
                    thread = pair,
                    users = users!!,
                    navHostController = navHostController,
                    userId = SharedPref.getUserName(context)
                )
            }
        }


    }

}


