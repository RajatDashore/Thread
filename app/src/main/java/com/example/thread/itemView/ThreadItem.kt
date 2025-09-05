package com.example.thread.itemView

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.media3.common.util.Log
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.thread.model.ThreadModel
import com.example.thread.model.UserModel


@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun ThreadItem(
    thread: ThreadModel, users: UserModel, navHostController: NavHostController, userId: String
) {
    val context = LocalContext.current

    Column() {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (userImage, userName, date, title, image) = createRefs()


            val painter = rememberAsyncImagePainter(model = thread.image)

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(painter)
                    .crossfade(true)
                    .build(),
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
                Text(
                    text = it, style = TextStyle(
                        fontSize = 20.sp
                    ), modifier = Modifier.constrainAs(userName) {
                        top.linkTo(userImage.top)
                        start.linkTo(userImage.end, margin = 12.dp)
                        bottom.linkTo(userImage.bottom)
                    })
            }


            thread.thread?.let {
                Text(
                    text = it, style = TextStyle(
                        fontSize = 18.sp
                    ), modifier = Modifier.constrainAs(title) {
                        top.linkTo(userName.bottom, 8.dp)
                        start.linkTo(userName.start)

                    })
            }


            if (thread.image != "") {
                Card(modifier = Modifier.constrainAs(image) {
                    top.linkTo(title.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
                    Log.d("IMAGE", thread.image.toString())
                    if (!thread.image.isNullOrEmpty()) {
                        AsyncImage(
                            model = thread.image,   // This is your Cloudinary URL nothing more than that
                            contentDescription = "Thread image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(8.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }


            }


        }


        Divider(color = Color.LightGray, thickness = 1.dp)
    }


}


