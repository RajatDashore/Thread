package com.example.thread.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.thread.R

@Composable
fun FullImage(imageUrl: String, navHostController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(5.dp)
    ) {
        // 🔹 Background image first
        AsyncImage(
            model = imageUrl,
            contentDescription = "Full Image",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(5.dp),
            contentScale = ContentScale.Fit
        )

        // 🔹 Close icon on top
        Icon(
            painter = painterResource(R.drawable.baseline_cancel_24),
            contentDescription = "Cancel",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .size(32.dp)
                .clickable {
                    navHostController.popBackStack() // better than navigate()
                },
            tint = Color.White
        )
    }
}
