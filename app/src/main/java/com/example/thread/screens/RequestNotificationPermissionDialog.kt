package com.example.thread.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.painterResource
import com.example.thread.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermissionDialog(
    openDialog: MutableState<Boolean>,
    permissionState: PermissionState
) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                permissionState.launchPermissionRequest()
            }, title = { Text("Notification Permission") },
            text = { Text("This app requires notification permission to show notification") },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.thread),
                    contentDescription = ""
                )
            }, confirmButton = {
                TextButton(onClick = {
                    openDialog.value = false
                    permissionState.launchPermissionRequest()
                }) {
                    Text(text = "Ok")
                }
            })

    }

}