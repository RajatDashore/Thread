package com.example.thread.itemView

import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.media3.common.util.Log
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.thread.model.ThreadModel
import com.example.thread.model.UserModel
import com.example.thread.navigation.Routes
import com.example.thread.utils.formatTimestamp


@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun ThreadItem(
    thread: ThreadModel,
    users: UserModel,
    navHostController: NavHostController,
    userId: String
) {
    val context = LocalContext.current
    val expanded = remember { mutableStateOf(false) }

    Column {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (userImage, userName, date, title, image, readMore) = createRefs()

            // 🔹 User profile image
            AsyncImage(
                model = users.imageUri,
                contentDescription = "Profile",
                modifier = Modifier
                    .constrainAs(userImage) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            // 🔹 Username (1 line max)
            users.username?.let {
                Text(
                    text = it,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    modifier = Modifier.constrainAs(userName) {
                        top.linkTo(userImage.top)
                        start.linkTo(userImage.end, margin = 12.dp)
                        bottom.linkTo(userImage.bottom)
                    }
                )
            }

            // 🔹 Date
            Text(
                text = formatTimestamp(thread.timeStemp.toString()),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.constrainAs(date) {
                    top.linkTo(userName.top)
                    bottom.linkTo(userName.bottom)
                    end.linkTo(parent.end, margin = 5.dp)
                }
            )


// 🔹 Thread text (expandable with "Read more")
            thread.thread?.let { text ->
                Text(
                    text = text,
                    style = TextStyle(fontSize = 18.sp),
                    maxLines = if (expanded.value) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis,   // ✅ Prevent overflow
                    modifier = Modifier.constrainAs(title) {
                        top.linkTo(userName.bottom, 8.dp)
                        start.linkTo(userName.start)
                        end.linkTo(parent.end) // ✅ also constrain horizontally
                        width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                    }
                )

                if (text.length > 100) { // show read more only if long text
                    Text(
                        text = if (expanded.value) "Show less" else "Read more",
                        color = Color.Blue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .constrainAs(readMore) {
                                top.linkTo(title.bottom, margin = 4.dp)
                                start.linkTo(title.start)
                            }
                            .padding(top = 4.dp)
                            .clickable { expanded.value = !expanded.value }
                    )
                }
            }


            // 🔹 Thread image
            if (!thread.image.isNullOrEmpty()) {
                Card(
                    modifier = Modifier.constrainAs(image) {
                        top.linkTo(title.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                ) {
                    AsyncImage(
                        model = thread.image,   // Cloudinary URL
                        contentDescription = "Thread image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(8.dp)
                            .clickable {
                                val encodedUrl = Uri.encode(thread.image)
                                val routes =
                                    Routes.FullImage.route.replace("{imageUrl}", encodedUrl)
                                navHostController.navigate(routes)
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Divider(color = Color.LightGray, thickness = 1.dp)
    }
}
