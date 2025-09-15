package com.example.thread.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.thread.R
import com.example.thread.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun Splash(navController: NavHostController) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        visible = true
        delay(1500) // show splash with fade-in for 1.5 sec
        if (FirebaseAuth.getInstance().currentUser != null) {
            navController.navigate(Routes.BottomNav.route) {
                navController.popBackStack()
                launchSingleTop = true
            }
        } else {
            navController.navigate(Routes.Login.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Image(
                painter = painterResource(R.drawable.thread),
                contentDescription = "logo",
                modifier = Modifier.size(120.dp)
            )
        }
    }
}
