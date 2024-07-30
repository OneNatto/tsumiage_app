package com.example.tsumiageapp.data.db

import androidx.room.*
import com.example.tsumiageapp.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCategory(categoryEntity: Category)

    @Delete
    suspend fun deleteCategory(categoryEntity: Category)

    @Query("SELECT * FROM category")
    fun getCategories(): Flow<List<Category>>
}