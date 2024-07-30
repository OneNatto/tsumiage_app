package com.example.tsumiageapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryType: String = "",
    val name: String = "",
)
