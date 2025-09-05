package com.example.thread.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatTimestamp(timestamp: String): String {
    val millis = timestamp.toLongOrNull() ?: return ""
    val instant = Instant.ofEpochMilli(millis)
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy,hh:mm a")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}
