package com.example.tsumiageapp.data.repository

import com.example.tsumiageapp.data.db.TaskDao
import com.example.tsumiageapp.data.model.Task
import com.example.tsumiageapp.ui.task.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(private val taskDao: TaskDao): TaskRepository {
    override suspend fun addTask(taskEntity: Task) {
        taskDao.addTask(taskEntity)
    }

    override suspend fun deleteTask(taskEntity: Task) {
        taskDao.deleteTask(taskEntity)
    }

    override fun getTasksByDate(date: LocalDate): Flow<List<Task>> {
        return taskDao.getTasksByDate(date.toString())
    }

    override fun getTasksBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<Task>> {
        return taskDao.getTasksBetweenDates(startDate.toString(),endDate.toString())
    }
}