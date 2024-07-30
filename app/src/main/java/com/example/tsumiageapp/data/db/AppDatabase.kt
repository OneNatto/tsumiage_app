package com.example.tsumiageapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tsumiageapp.data.model.Category
import com.example.tsumiageapp.data.model.Goal
import com.example.tsumiageapp.data.model.Task

@Database(entities = [Task::class, Category::class,Goal::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun goalDao(): GoalDao

    companion object{
        @Volatile
        private var Instances: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instances ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext,AppDatabase::class.java,"database")
                    .fallbackToDestructiveMigration()
                    .build().also {
                        Instances = it
                    }
            }
        }
    }
}