package com.example.thread.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.thread.R
import com.example.thread.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun Splash(navController: NavHostController) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (image) = createRefs()
        Image(
            painter = painterResource(R.drawable.thread_logo),
            contentDescription = "logo",
            modifier = Modifier
                .size(120.dp)
                .background(Color.White)
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
        )
    }

    LaunchedEffect(true) {
        delay(1000)
        if (FirebaseAuth.getInstance().currentUser != null) {
            navController.navigate(Routes.BottomNav.route)
        } else {
            navController.navigate(Routes.Login.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }


    }
}