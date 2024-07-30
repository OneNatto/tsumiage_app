package com.example.tsumiageapp.ui.common.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.AssistantPhoto
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

enum class NaviItemEnum(
    val id: String,
    val icon: ImageVector,
    val label: String
) {
    Home(
        id = "home",
        icon = Icons.Outlined.Home,
        label = "ホーム"
    ),
    Goal(
        id = "goal",
        icon = Icons.Outlined.AssistantPhoto,
        label = "目標"
    ),
    Report(
        id = "report",
        icon = Icons.Outlined.Analytics,
        label = "過去のデータ"
    )

}