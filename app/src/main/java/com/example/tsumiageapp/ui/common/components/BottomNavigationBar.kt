package com.example.tsumiageapp.ui.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tsumiageapp.ui.common.enums.NaviItemEnum

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    navController: NavController
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.graphicsLayer {
            shadowElevation = 12.dp.toPx()
        }
    ) {
        NaviItemEnum.entries.forEach { item ->
            var iconSize = 20.dp
            if(currentRoute == item.id) {
                iconSize = 24.dp
            }
            NavigationBarItem(
                icon = {
                       Column(
                           horizontalAlignment = Alignment.CenterHorizontally
                       ) {
                           Icon(
                               imageVector = item.icon,
                               contentDescription = item.label,
                               modifier = Modifier.size(iconSize)
                           )
                           Text(
                               item.label,
                               fontSize = if(currentRoute == item.id) 15.sp else 14.sp ,
                               fontWeight = if(currentRoute == item.id) FontWeight.Bold else FontWeight.Light
                           )
                       }
                },
                selected = currentRoute == item.id,
                onClick = {
                    navController.navigate(item.id)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.DarkGray,
                    indicatorColor = MaterialTheme.colorScheme.background
                )
            )
        }
    }
}