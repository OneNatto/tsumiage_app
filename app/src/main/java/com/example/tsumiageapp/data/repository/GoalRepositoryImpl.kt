package com.example.tsumiageapp.data.repository

import com.example.tsumiageapp.data.db.GoalDao
import com.example.tsumiageapp.data.model.Goal
import com.example.tsumiageapp.ui.goal.GoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(private val goalDao: GoalDao) : GoalRepository {
    override suspend fun addGoal(goalEntity: Goal) = goalDao.addGoal(goalEntity)

    override fun getGoalByType(goalType: String): Flow<List<Goal>> = goalDao.getGoalByType(goalType)

    override suspend fun deleteGoal(goalEntity: Goal) = goalDao.deleteGoal(goalEntity)
}