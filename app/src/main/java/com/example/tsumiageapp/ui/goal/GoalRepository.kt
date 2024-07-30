package com.example.tsumiageapp.ui.goal

import com.example.tsumiageapp.data.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    suspend fun addGoal(goalEntity: Goal)

    suspend fun deleteGoal(goalEntity: Goal)

    fun getGoalByType(goalType: String): Flow<List<Goal>>
}