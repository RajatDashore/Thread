package com.example.thread.itemView

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.thread.R
import com.example.thread.application.ThreadApplication
import com.example.thread.model.UserModel
import com.example.thread.navigation.Routes

@Composable
fun UserItem(
    users: UserModel,
    navHostController: NavHostController
) {
    Column {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    val routes = Routes.OtherUsers.route.replace("{data}", users.uid ?: "")
                    navHostController.navigate(routes)
                }
        ) {
            val (userImage, userName, title) = createRefs()

            // Use AsyncImage directly with URL/Uri
            users.imageUri?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    imageLoader = ThreadApplication.imageLoader,
                    placeholder = painterResource(R.drawable.baseline_person_24),
                    error = painterResource(R.drawable.baseline_person_24),
                    contentDescription = "User Profile",
                    modifier = Modifier
                        .constrainAs(userImage) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            users.username?.let { username ->
                Text(
                    text = username,
                    style = TextStyle(fontSize = 20.sp),
                    modifier = Modifier.constrainAs(userName) {
                        top.linkTo(userImage.top)
                        start.linkTo(userImage.end, margin = 12.dp)
                    }
                )
            }

            users.name?.let { name ->
                Text(
                    text = name,
                    style = TextStyle(fontSize = 17.sp),
                    modifier = Modifier.constrainAs(title) {
                        top.linkTo(userName.bottom, margin = 2.dp)
                        start.linkTo(userName.start)
                    }
                )
            }
        }

        Divider(color = Color.LightGray, thickness = 1.dp)
    }
}
