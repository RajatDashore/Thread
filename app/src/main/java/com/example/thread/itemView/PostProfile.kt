package com.example.thread.itemView

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.thread.R
import com.example.thread.application.ThreadApplication
import com.example.thread.navigation.Routes


@Composable
fun PostProfile(image: String, caption: String, navHostController: NavHostController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        AsyncImage(
            model = image,
            imageLoader = ThreadApplication.imageLoader,
            modifier = Modifier
                .height(200.dp)
                .width(120.dp)
                .clip(RoundedCornerShape(5.dp))
                .clickable {
                    val encodedUrl = Uri.encode(image)
                    val routes =
                        Routes.FullImage.route.replace("{imageUrl}", encodedUrl)
                    navHostController.navigate(routes)
                }
                .border(
                    width = 1.dp, color = Color.LightGray
                ),
            contentDescription = "Post",
            placeholder = painterResource(R.drawable.baseline_person_24),
            contentScale = ContentScale.Crop
        )
        Text(
            text = caption,
            modifier = Modifier.padding(2.dp),
            fontSize = 10.sp,
            maxLines = 1,
        )
    }
}