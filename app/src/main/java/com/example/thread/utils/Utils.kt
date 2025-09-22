
package com.example.thread.utils

import android.util.Log
import com.example.thread.application.ThreadApplication
import com.example.thread.model.NotificationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.functions.FirebaseFunctions
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

val TAG = "Utils"
fun formatTimestamp(timestamp: String): String {
    val millis = timestamp.toLongOrNull() ?: return ""
    val instant = Instant.ofEpochMilli(millis)
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy,hh:mm a")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}


fun getTimeAgo(timestampString: String?): String {
    if (timestampString.isNullOrEmpty()) return "Just now"

    return try {
        val timestamp = timestampString.toLongOrNull() ?: return "Just now"
        val now = System.currentTimeMillis()

        if (timestamp > now || timestamp <= 0) return "Just now"

        val diff = now - timestamp

        when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "$minutes minute${if (minutes > 1) "s" else ""}"
            }

            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "$hours hour${if (hours > 1) "s" else ""}"
            }

            diff < TimeUnit.DAYS.toMillis(30) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "$days day${if (days > 1) "s" else ""}"
            }

            diff < TimeUnit.DAYS.toMillis(365) -> {
                val months = TimeUnit.MILLISECONDS.toDays(diff) / 30
                "$months month${if (months > 1) "s" else ""}"
            }

            else -> {
                val years = TimeUnit.MILLISECONDS.toDays(diff) / 365
                "$years year${if (years > 1) "s" else ""}"
            }
        }
    } catch (e: Exception) {
        "Just now"
    }
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

    FirebaseFunctions.getInstance().getHttpsCallable("sendGlobalNotification").call(data)
        .addOnSuccessListener {
            Log.d(TAG, "sendBroadCastNotification: Message Sent ")
        }.addOnFailureListener {
            Log.d(TAG, "sendBroadCastNotification: Message failed to send")
        }
}
