package com.example.thread.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserWithThreads(
    var user: UserModel,
    var threads: List<ThreadModel>
)
