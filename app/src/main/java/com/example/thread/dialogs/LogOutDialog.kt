package com.example.thread.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState


@Composable
fun LogOutDialog(
    openDialog: MutableState<Boolean>,
    title: String = "Confirm",
    message: String = "Are you sure?",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
            onDismiss()
        }, title = {
            Text(text = title)
        }, text = {
            Text(text = message)
        }, confirmButton = {
            TextButton(onClick = {
                openDialog.value = false
                onConfirm()
            }) {
                Text(text = "Yes")
            }
        }, dismissButton = {
            TextButton(onClick = {
                openDialog.value = false
                onDismiss()
            }) {
                Text(text = "No")
            }
        }
        )
    }
}