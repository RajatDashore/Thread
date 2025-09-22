package com.example.thread.itemView

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.thread.R
import com.example.thread.utils.getTimeAgo


@Composable
fun NotificationItem(image: String, message: String, timeStemp: String) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {


        if (image != null || image == "") {
            AsyncImage(
                model = image,
                contentDescription = "UserImagel",
                modifier = Modifier
                    .size(40.dp)
                    .clip(
                        shape = CircleShape
                    ),
                placeholder = painterResource(R.drawable.baseline_person_24),
                error = painterResource(R.drawable.baseline_person_24)
            )
        }




        Column() {
            Text(text = message, fontWeight = FontWeight.Bold, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = getTimeAgo(timeStemp), fontWeight = FontWeight.Medium, fontSize = 12.sp)
        }
    }
}