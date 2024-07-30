package com.example.tsumiageapp.ui.category

import com.example.tsumiageapp.data.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun addCategory(categoryEntity: Category)

    suspend fun deleteCategory(categoryEntity: Category)

    fun getCategories(): Flow<List<Category>>
}