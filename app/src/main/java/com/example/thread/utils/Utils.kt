
package com.example.thread.utils

import android.util.Log
import com.example.thread.application.ThreadApplication
import com.google.firebase.functions.FirebaseFunctions
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


val TAG = "Utils"
fun formatTimestamp(timestamp: String): String {
    val millis = timestamp.toLongOrNull() ?: return ""
    val instant = Instant.ofEpochMilli(millis)
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy,hh:mm a")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}


fun RemoveAllCacheImage() {
    ThreadApplication().ClearAllCacheImages()
}


fun sendNotificationToOneUser(targetToken: String, title: String, body: String) {
    val functions = FirebaseFunctions.getInstance()
    val data = hashMapOf(
        "token" to targetToken, "title" to title, "body" to body
    )

    functions.getHttpsCallable("sendNotification").call(data).addOnSuccessListener {
        Log.d(TAG, "sendNotificationToOtherUser: ")
    }.addOnFailureListener {
        Log.d(TAG, "notificationSendFailed: ")
    }
}


fun sendBroadCastNotification(title: String, body: String) {
    val data = hashMapOf(
        "title" to title, "body" to body
    )

    FirebaseFunctions.getInstance().getHttpsCallable("sendGloblaNotification").call(data)
        .addOnSuccessListener {
            Log.d(TAG, "sendBroadCastNotification: Message Sent ")
        }.addOnFailureListener {
            Log.d(TAG, "sendBroadCastNotification: Message failed to send")
        }
}
