package com.example.thread.model

data class UserWithThreads(
    val user: UserModel,
    val threads: List<ThreadModel>
)