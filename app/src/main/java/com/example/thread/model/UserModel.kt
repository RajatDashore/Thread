package com.example.thread.model

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class UserModel(
    var email: String? = "",
    var pass: String? = "",
    var name: String? = "",
    var bio: String? = "",
    var username: String? = "",
    var imageUri: String? = "",
    var uid: String? = "",
)