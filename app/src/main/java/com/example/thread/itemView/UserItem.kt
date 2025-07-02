package com.example.thread.itemView

import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.example.thread.model.UserModel
import com.example.thread.navigation.Routes


@Composable
fun UserItem(
    users: UserModel, navHostController: NavHostController
) {

    Column() {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    val routes = Routes.OtherUsers.route.replace("{data}", users.uid!!)
                    navHostController.navigate(routes)

                }
        ) {
            val (userImage, userName, date, title, image) = createRefs()

            Image(painter = rememberAsyncImagePainter(model = users.imageUri!!),
                contentDescription = "Logo",
                modifier = Modifier
                    .constrainAs(userImage) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop)

            users.username?.let {
                Text(text = it, style = TextStyle(
                    fontSize = 20.sp
                ), modifier = Modifier.constrainAs(userName) {
                    top.linkTo(userImage.top)
                    start.linkTo(userImage.end, margin = 12.dp)
                })
            }


            users.name?.let {
                Text(text = it, style = TextStyle(
                    fontSize = 17.sp
                ), modifier = Modifier.constrainAs(title) {
                    top.linkTo(userName.bottom, 2.dp)
                    start.linkTo(userName.start)

                })
            }


        }


    }


    Divider(color = Color.LightGray, thickness = 1.dp)
}




