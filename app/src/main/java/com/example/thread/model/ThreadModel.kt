package com.example.thread.model

import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.serialization.Serializable

@IgnoreExtraProperties
@Serializable
data class ThreadModel(
    var threadId: String? = null,
    var thread: String? = null,
    var image: String? = null,
    var userId: String? = null,
    var timeStemp: String? = null,
    var Likes: Map<String, Boolean>? = null,
    var Comments: Map<String, CommentModel>? = null
) {
    constructor() : this(null, null, null, null, null, null, null) // Needed for Firebase

    // Computed properties must be ignored by Firebase
    @get:com.google.firebase.database.Exclude
    val likeCount: Int get() = Likes?.size ?: 0

    @get:com.google.firebase.database.Exclude
    val commentCount: Int get() = Comments?.size ?: 0
}
