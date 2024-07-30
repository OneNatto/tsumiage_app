package com.example.tsumiageapp.ui.common.functions

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

private val dayFormatter = DateTimeFormatter.ofPattern("M月d日")

fun setTermTextByDate(date: LocalDate): String {
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.JAPANESE)
    val formattedTodayDate = date.format(dayFormatter)

    return "$formattedTodayDate（$dayOfWeek）"
}
