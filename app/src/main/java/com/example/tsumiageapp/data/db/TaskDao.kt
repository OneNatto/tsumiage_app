package com.example.tsumiageapp.data.db

import androidx.room.*
import com.example.tsumiageapp.data.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(taskEntity: Task)

    @Delete
    suspend fun deleteTask(taskEntity: Task)

    @Query("SELECT * from task WHERE date = :date")
    fun getTasksByDate(date: String) : Flow<List<Task>>

    @Query("SELECT * from task WHERE date BETWEEN :start AND :end ORDER BY date")
    fun getTasksBetweenDates(start: String, end: String) : Flow<List<Task>>
}