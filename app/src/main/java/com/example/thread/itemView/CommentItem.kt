package com.example.thread.itemView


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.thread.R
import com.example.thread.model.CommentWithUser
import com.example.thread.utils.getTimeAgo

@Composable
fun CommentItem(commentWithUser: CommentWithUser) {
    val user = commentWithUser.userModel
    val comment = commentWithUser.commentModel

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        AsyncImage(
            model = user?.imageUri,
            contentDescription = "User Image",
            placeholder = painterResource(
                R.drawable.baseline_person_24
            ),
            error = painterResource(R.drawable.baseline_person_24),
            modifier = Modifier
                .size(36.dp)
                .clip(
                    CircleShape
                ), contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = user!!.name ?: "User ",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = comment!!.text ?: "", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = getTimeAgo(comment.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

        }
    }

    HorizontalDivider(modifier = Modifier.height(1.dp))
}