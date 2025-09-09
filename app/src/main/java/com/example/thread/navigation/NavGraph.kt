package com.example.thread.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.thread.screens.AddThread
import com.example.thread.screens.BottomNav
import com.example.thread.screens.FullImage
import com.example.thread.screens.Home
import com.example.thread.screens.Login
import com.example.thread.screens.Notification
import com.example.thread.screens.OtherUsers
import com.example.thread.screens.Profile
import com.example.thread.screens.Register
import com.example.thread.screens.Search
import com.example.thread.screens.Splash


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, Routes.Splash.route) {
        composable(Routes.Splash.route) {
            Splash(navController)
        }
        composable(Routes.Home.route) {
            Home(navController)
        }
        composable(Routes.Notification.route) {
            Notification()
        }
        composable(Routes.Search.route) {
            Search(navController)
        }
        composable(Routes.Login.route) {
            Login(navController)
        }

        composable(Routes.Register.route) {
            Register(navController)
        }
        composable(Routes.Profile.route) {
            Profile(navController)
        }
        composable(Routes.AddThread.route) {
            AddThread(navController)
        }
        composable(Routes.BottomNav.route) {
            BottomNav(navController)
        }
        composable(Routes.OtherUsers.route) {
            val data = it.arguments!!.getString("data")
            OtherUsers(navController, data)
        }

        composable(Routes.FullImage.route) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
            val imageUrl = encodedUrl?.let { android.net.Uri.decode(it) }
            FullImage(imageUrl = imageUrl!!, navController)
        }

    }


}