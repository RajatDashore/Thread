package com.example.thread.itemView

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.thread.R
import com.example.thread.application.ThreadApplication
import com.example.thread.model.ThreadModel
import com.example.thread.model.UserModel
import com.example.thread.navigation.Routes
import com.example.thread.utils.getTimeAgo

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun ThreadItem(
    thread: ThreadModel, users: UserModel, navHostController: NavHostController, userId: String
) {
    val context = LocalContext.current
    val expanded = remember { mutableStateOf(false) }
    var likes: String by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (userImage, userName, date, title, readMore, image, likeRow) = createRefs()

            // 🔹 User profile image
            AsyncImage(
                model = users.imageUri,
                contentDescription = "Profile",
                imageLoader = ThreadApplication.imageLoader,
                modifier = Modifier
                    .padding(8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .constrainAs(userImage) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    },
                error = painterResource(R.drawable.baseline_person_24),
                placeholder = painterResource(R.drawable.baseline_person_24),
                contentScale = ContentScale.Crop
            )

            // 🔹 Username
            users.username?.let {
                Text(
                    text = it,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    modifier = Modifier.constrainAs(userName) {
                        start.linkTo(userImage.end, margin = 8.dp)
                        top.linkTo(userImage.top)
                        bottom.linkTo(userImage.bottom)
                    })
            }

            // 🔹 Date
            Text(
                text = getTimeAgo(thread.timeStemp.toString()),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium, color = Color.Gray,
                modifier = Modifier.constrainAs(date) {
                    end.linkTo(parent.end, margin = 8.dp)
                    top.linkTo(userName.top)
                    bottom.linkTo(userName.bottom)
                })

            // 🔹 Thread text
            thread.thread?.let { text ->
                Text(
                    text = text,
                    style = TextStyle(fontSize = 16.sp),
                    maxLines = if (expanded.value) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.constrainAs(title) {
                        top.linkTo(userName.bottom, margin = 5.dp)
                        start.linkTo(userName.start)
                        end.linkTo(parent.end, margin = 8.dp)
                        width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                    })

                if (text.length > 100) {
                    Text(
                        text = if (expanded.value) "Show less" else "Read more",
                        color = Color(0xFF2575FC),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable { expanded.value = !expanded.value }
                            .constrainAs(readMore) {
                                top.linkTo(title.bottom, margin = 4.dp)
                                start.linkTo(title.start)
                            })
                }
            }

            // 🔹 Thread image (edge-to-edge, no padding)
            if (!thread.image.isNullOrEmpty()) {
                AsyncImage(
                    model = thread.image,
                    contentDescription = "Thread image",
                    imageLoader = ThreadApplication.imageLoader,
                    placeholder = painterResource(R.drawable.baseline_person_24),
                    error = painterResource(R.drawable.baseline_person_24),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clickable {
                            val encodedUrl = Uri.encode(thread.image)
                            val routes = Routes.FullImage.route.replace("{imageUrl}", encodedUrl)
                            navHostController.navigate(routes)
                        }
                        .constrainAs(image) {
                            top.linkTo(title.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    contentScale = ContentScale.Crop
                )
            }

            // 🔹 Like row (spaced and aligned under image)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .constrainAs(likeRow) {
                        top.linkTo(image.bottom, margin = 6.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(R.drawable.heart_red_svgrepo_com),
                    contentDescription = "Likes", modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            Toast.makeText(context, "Liked the thread", Toast.LENGTH_SHORT).show()
                        }
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = likes, fontSize = 13.sp, fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.size(15.dp))

                Image(
                    painter = painterResource(R.drawable.comment),
                    contentDescription = "Comment",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface)
                )

                Spacer(Modifier.size(6.dp))

                Text(
                    text = likes, fontSize = 13.sp, fontWeight = FontWeight.SemiBold
                )

            }
        }

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.6f), thickness = 1.dp)
    }
}

