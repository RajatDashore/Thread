package com.example.thread.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class BottomNavItem(
    var title: String = "",
    var route: String = "",
    var icon: ImageVector

)