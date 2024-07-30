package com.example.tsumiageapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val categoryType: String = "",
    val categoryName: String = "",
    val time: Int?,
    val date: String = ""
)

