package com.example.thread.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ThreadModel(
    val threadId: String? = null,
    var thread: String? = null,
    var image: String? = null,
    var userId: String? = null,
    var timeStemp: String? = null,
)
