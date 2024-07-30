package com.example.tsumiageapp.data.repository

import com.example.tsumiageapp.data.db.CategoryDao
import com.example.tsumiageapp.data.model.Category
import com.example.tsumiageapp.ui.category.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(private val categoryDao: CategoryDao) : CategoryRepository {
    override suspend fun addCategory(categoryEntity: Category) {
        categoryDao.addCategory(categoryEntity)
    }

    override suspend fun deleteCategory(categoryEntity: Category) {
        categoryDao.deleteCategory(categoryEntity)
    }

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getCategories()
    }
}