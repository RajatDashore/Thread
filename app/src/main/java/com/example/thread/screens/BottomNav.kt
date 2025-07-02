package com.example.thread.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thread.model.BottomNavItem
import com.example.thread.navigation.Routes

@Composable
fun BottomNav(navController: NavHostController) {
    val navController1 = rememberNavController()

    Scaffold(bottomBar = { MyBottomBar(navController1) }) { innerPadding ->
        NavHost(
            navController = navController1,
            startDestination = Routes.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Routes.Home.route) {
                Home(navController)
            }

            composable(route = Routes.Search.route) {
                Search(navController)
            }
            composable(route = Routes.AddThread.route) {
                AddThread(navController1)
            }
            composable(route = Routes.Notification.route) {
                Notification()
            }
            composable(route = Routes.Profile.route) {
                Profile(navController)
            }

        }
    }
}

@Composable
fun MyBottomBar(navController1: NavHostController) {

    val backStackEntry = navController1.currentBackStackEntry

    val list = listOf(
        BottomNavItem("Home", Routes.Home.route, Icons.Rounded.Home),
        BottomNavItem(
            "Search", Routes.Search.route, Icons.Rounded.Search
        ),
        BottomNavItem("Add Thread", Routes.AddThread.route, Icons.Rounded.Add),
        BottomNavItem("Notification", Routes.Notification.route, Icons.Rounded.Notifications),
        BottomNavItem("Profile", Routes.Profile.route, Icons.Rounded.Person)
    )

    BottomAppBar {
        list.forEach {
            val selected = it.route == backStackEntry?.destination?.route

            NavigationBarItem(selected = selected, onClick = {
                navController1.navigate(it.route) {
                    popUpTo(navController1.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                }
            }, icon = {
                Icon(imageVector = it.icon, contentDescription = it.title)
            })
        }
    }

}
