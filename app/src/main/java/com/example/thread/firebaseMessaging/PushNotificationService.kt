package com.example.thread.firebaseMessaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.thread.MainActivity
import com.example.thread.R
import com.example.thread.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


val TAG = "HANDLE_TAG"

class PushNotificationService : FirebaseMessagingService() {
    private val CHANNEL_ID = "notification_channel"
    private val CHANNEL_NAME = "com.example.thread"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.let {
            showNotification(it)
        }

        if (message.data.isNotEmpty()) {
            handleDataMessage()
        }
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseDatabase.getInstance().reference.child(Constants.USERS).child(uid).child("Token")
            .setValue(token)
        Log.d(TAG, "onNewToken: $token")
    }


    private fun handleDataMessage() {
        Log.d(TAG, "handleDataMessage: ")
    }


    fun showNotification(message: RemoteMessage.Notification) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        var notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle(message.title)
                .setContentText(message.body).setSmallIcon(R.drawable.thread_logo)
                .setAutoCancel(true).setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                .setOnlyAlertOnce(true).setContentIntent(pendingIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        manager.notify(Random.nextInt(), notificationBuilder.build())
    }
}
