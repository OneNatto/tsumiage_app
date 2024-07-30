package com.example.tsumiageapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val title: String,
    val time: Int,
    val categoryType: String,
    val categoryName: String,
    val goalType: String,
    val addDate: String,
    val isDone: Boolean
)
