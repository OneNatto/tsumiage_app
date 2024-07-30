package com.example.tsumiageapp.ui.task

import com.example.tsumiageapp.data.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

interface TaskRepository {
    suspend fun addTask(taskEntity: Task)

    suspend fun deleteTask(taskEntity: Task)

    fun getTasksByDate(date: LocalDate): Flow<List<Task>>

    fun getTasksBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<Task>>
}