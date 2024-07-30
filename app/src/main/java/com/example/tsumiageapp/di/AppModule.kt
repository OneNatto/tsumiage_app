package com.example.tsumiageapp.di

import android.content.Context
import com.example.tsumiageapp.data.db.AppDatabase
import com.example.tsumiageapp.data.db.CategoryDao
import com.example.tsumiageapp.data.db.GoalDao
import com.example.tsumiageapp.data.db.TaskDao
import com.example.tsumiageapp.data.repository.CategoryRepositoryImpl
import com.example.tsumiageapp.data.repository.GoalRepositoryImpl
import com.example.tsumiageapp.data.repository.TaskRepositoryImpl
import com.example.tsumiageapp.ui.category.CategoryRepository
import com.example.tsumiageapp.ui.goal.GoalRepository
import com.example.tsumiageapp.ui.task.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideGoalDao(database: AppDatabase): GoalDao {
        return database.goalDao()
    }

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository {
        return TaskRepositoryImpl(taskDao)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository {
        return CategoryRepositoryImpl(categoryDao)
    }

    @Provides
    @Singleton
    fun provideGoalRepository(goalDao: GoalDao): GoalRepository {
        return GoalRepositoryImpl(goalDao)
    }
}