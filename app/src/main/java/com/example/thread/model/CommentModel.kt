package com.example.thread.model

import kotlinx.serialization.Serializable


@Serializable
data class CommentModel(
    val commentId: String? = "",
    val userId: String? = null,
    val text: String? = null,
    val timestamp: String? = null
)
