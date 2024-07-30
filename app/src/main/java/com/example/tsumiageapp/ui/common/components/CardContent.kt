package com.example.tsumiageapp.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun CardContent(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = ShapeDefaults.ExtraSmall,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = modifier
    ) {
        content()
    }
}