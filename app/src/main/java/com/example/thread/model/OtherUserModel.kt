package com.example.thread.model

import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.serialization.Serializable


@IgnoreExtraProperties
@Serializable
data class OtherUserModel(
    var uid: String = "",
    var userName: String = "",
    var date: String = ""
)



