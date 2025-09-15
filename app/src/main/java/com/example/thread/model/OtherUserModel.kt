package com.example.thread.model

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class OtherUserModel(
    var uid: String = "",
    var userName: String = "",
    var date: String = ""
)



