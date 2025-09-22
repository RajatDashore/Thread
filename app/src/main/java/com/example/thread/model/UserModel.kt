package com.example.thread.model

import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.serialization.Serializable

@IgnoreExtraProperties
@Serializable
data class UserModel(
    var email: String? = null,
    var pass: String? = null,
    var name: String? = null,
    var bio: String? = null,
    var username: String? = null,
    var imageUri: String? = null,
    var uid: String? = null,
) {
    // Explicit no-arg constructor for Firebase
    constructor() : this(null, null, null, null, null, null, null)
}
