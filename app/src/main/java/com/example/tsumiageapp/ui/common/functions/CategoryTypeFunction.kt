package com.example.tsumiageapp.ui.common.functions

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.room.util.createCancellationSignal
import com.example.tsumiageapp.R
import com.example.tsumiageapp.data.model.CategoryTypeEnum
import kotlinx.coroutines.joinAll

fun getCategoryTypeImage(type: CategoryTypeEnum): Int {
    return when(type) {
        CategoryTypeEnum.STUDY -> {
            R.drawable.study
        }

        CategoryTypeEnum.EXERCISE -> {
            R.drawable.exercise
        }

        CategoryTypeEnum.HOUSEWORK -> {
            R.drawable.housework
        }
    }
}

fun getCategoryBackGroundColor(type: CategoryTypeEnum): Color {
    return when(type) {
        CategoryTypeEnum.STUDY -> {
            Color(red = 0xFF,green = 0x1F,blue = 0x00).copy(alpha = 0.2F)
        }

        CategoryTypeEnum.EXERCISE -> {
            Color(red = 0xFF,green = 0x9F,blue = 0x00).copy(alpha = 0.3F)
        }

        CategoryTypeEnum.HOUSEWORK -> {
            Color(red = 0xFF,green = 0xDF,blue = 0x00).copy(alpha = 0.35F)
        }
    }
}

fun getEachTaskBackGroundColor(type: CategoryTypeEnum): Color {
    return when(type) {
        CategoryTypeEnum.STUDY -> {
            Color(red = 0xFF,green = 0x1F,blue = 0x00).copy(alpha = 0.04F)
        }

        CategoryTypeEnum.EXERCISE -> {
            Color(red = 0xFF,green = 0x9F,blue = 0x00).copy(alpha = 0.1F)
        }

        CategoryTypeEnum.HOUSEWORK -> {
            Color(red = 0xFF,green = 0xDF,blue = 0x00).copy(alpha = 0.15F)
        }
    }
}