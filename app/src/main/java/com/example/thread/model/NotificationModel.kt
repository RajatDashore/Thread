package com.example.thread.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationModel(
    var currentUid: String? = "",
    var notification: String? = "",
    var time: String? = "",
    var image: String? = ""
)