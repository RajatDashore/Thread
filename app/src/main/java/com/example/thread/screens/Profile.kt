package com.example.thread.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import coil3.compose.AsyncImage
import com.example.thread.R
import com.example.thread.itemView.ThreadItem
import com.example.thread.model.UserModel
import com.example.thread.navigation.Routes
import com.example.thread.utils.RemoveAllCacheImage
import com.example.thread.utils.SharedPref
import com.example.thread.viewModel.AuthViewModel
import com.example.thread.viewModel.OtherUserViewModel
import com.example.thread.viewModel.PostProfileViewModel
import com.example.thread.viewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun Profile(navHostController: NavHostController) {
    val userViewModel: UserViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val threads by userViewModel.threads.observeAsState(null)
    val firebaseUser by authViewModel.firebaseUser.observeAsState(null)
    val otherUserViewModel: OtherUserViewModel = viewModel()
    val follower by otherUserViewModel.followerCount.observeAsState(0)
    val follewing by otherUserViewModel.followingCount.observeAsState(0)
    val postViewModel: PostProfileViewModel = viewModel()
    val post by postViewModel.post.collectAsState()
    val context = LocalContext.current

    val user = UserModel(
        name = SharedPref.getName(context),
        username = SharedPref.getUserName(context),
        imageUri = SharedPref.getImage(context),
    )

    LaunchedEffect(firebaseUser) {
        if (firebaseUser == null) {
            navHostController.navigate(Routes.Login.route) {
                popUpTo(navHostController.graph.startDestinationId)
                launchSingleTop = true
            }
        } else {
            userViewModel.fetchThread(FirebaseAuth.getInstance().currentUser!!.uid)
            postViewModel.getAllPost()
            otherUserViewModel.fetchFollowersAndFollowingCounts(FirebaseAuth.getInstance().currentUser!!.uid)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            item {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    val (text, logo, userName, bio, followers, following, imageBox, LogButton) = createRefs()


                    Text(
                        text = SharedPref.getName(context), style = TextStyle(
                            fontWeight = FontWeight.Bold, fontSize = 24.sp
                        ), modifier = Modifier.constrainAs(text) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        })


                    AsyncImage(
                        model = user.imageUri,
                        contentDescription = "Profile Image",
                        placeholder = painterResource(R.drawable.baseline_person_24),
                        error = painterResource(R.drawable.baseline_person_24),
                        modifier = Modifier
                            .constrainAs(logo) {
                                top.linkTo(text.bottom)
                                end.linkTo(parent.end)
                            }
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient( // or radialGradient
                                    colors = listOf(
                                        Color.Magenta,
                                        Color.Cyan,
                                        Color.Blue
                                    ) // gradient colors
                                ),
                                shape = CircleShape // or RoundedCornerShape(12.dp)
                            )
                            .clip(CircleShape)
                            .clickable {
                                val encodedUrl = Uri.encode(user.imageUri)
                                val routes =
                                    Routes.FullImage.route.replace("{imageUrl}", encodedUrl)
                                navHostController.navigate(routes)
                            }
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop)

                    Text(
                        text = SharedPref.getUserName(context), style = TextStyle(
                            fontSize = 20.sp
                        ), modifier = Modifier.constrainAs(userName) {
                            top.linkTo(text.bottom)
                            start.linkTo(parent.start)
                        })

                    Text(
                        text = SharedPref.getBio(context), style = TextStyle(
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
                                authViewModel.logout()
                                SharedPref.removeUserInfoFormSharedPref(context)
                                RemoveAllCacheImage()
                            }, modifier = Modifier.padding(top = 5.dp)
                        ) {
                            Text("Logout", color = Color.Blue)
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
            items(threads ?: emptyList()) { pair ->
                ThreadItem(
                    thread = pair,
                    users = user,
                    navHostController = navHostController,
                    userId = SharedPref.getUserName(context)
                )
            }

        }

        if (post.isNotEmpty()) {
            Text(
                text = "Posts",
                modifier = Modifier.padding(2.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3)
            ) {
                items(post) { photo ->
                    PostProfile(photo.image.toString(), photo.thread.toString(), navHostController)
                }
            }
        } else {
            Text(text = "Nothing is posted by user", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }


}

