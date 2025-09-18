package com.example.thread.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ThreadModel(
    val threadId: String? = "",
    var thread: String? = null,
    var image: String? = null,
    var userId: String? = null,
    var timeStemp: String? = null,
    var Likes: Map<String, Boolean> = emptyMap(),
    var Comments: Map<String, CommentModel> = emptyMap()
) {
    val likeCount: Int get() = Likes.size
    val commentCount: Int get() = Comments.size
}
