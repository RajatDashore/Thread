package com.example.thread.model

import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.serialization.Serializable

@IgnoreExtraProperties
@Serializable
data class UserWithThreads(
    var user: UserModel,
    var threads: List<ThreadModel>
)
