package com.example.thread.screens

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.thread.R
import com.example.thread.application.ThreadApplication

@SuppressLint("UnusedBoxWithConstraintsScope", "UnrememberedMutableState")
@Composable
fun FullImage(imageUrl: String, navHostController: NavHostController) {
    var isLiked by remember { mutableStateOf(false) }
    var scl by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val heartColor by animateColorAsState(
        targetValue = if (isLiked) Color.Red else Color.White,
        label = "heartColor"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(5.dp)
    ) {
        // 🔹 Background image
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val state = rememberTransformableState { zoomChange, panChange, rotationChange ->
                scl = (scl * zoomChange).coerceIn(1f, 5f)
                val extraWidth = (scl - 1) * constraints.maxWidth
                val extraHeight = (scl - 1) * constraints.maxHeight
                val maxX = extraWidth / 2
                val maxY = extraHeight / 2
                offset = Offset(
                    x = (offset.x + scl * panChange.x).coerceIn(-maxX, maxX),
                    y = (offset.y + scl * panChange.y).coerceIn(-maxY, maxY)
                )
            }
            AsyncImage(
                model = imageUrl,
                imageLoader = ThreadApplication.imageLoader,
                contentDescription = "Full Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .graphicsLayer {
                        scaleX = scl
                        scaleY = scl
                        translationX = offset.x
                        translationY = offset.y
                    }
                    .transformable(state)
                    .padding(5.dp),
                contentScale = ContentScale.Fit
            )
        }

        // 🔹 Close icon on top
        Icon(
            painter = painterResource(R.drawable.baseline_cancel_24),
            contentDescription = "Cancel",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .size(32.dp)
                .clickable {
                    navHostController.popBackStack()
                },
            tint = Color.White
        )
    }
}
