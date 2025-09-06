package com.example.thread.navigation

sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Login : Routes("login")
    object Register : Routes("register")
    object Notification : Routes("notification")
    object Search : Routes("search")
    object Profile : Routes("profile")
    object Splash : Routes("splash")
    object AddThread : Routes("addThread")
    object BottomNav : Routes("bottomNav")
    object OtherUsers : Routes("other_users/{data}")
    object FullImage : Routes("fullImage/{imageUrl}")
}