package com.example.tsumiageapp.data.db

import androidx.room.*
import com.example.tsumiageapp.data.model.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGoal(goalEntity: Goal)

    @Query("SELECT * FROM goal WHERE goalType = :goalType")
    fun getGoalByType(goalType: String): Flow<List<Goal>>

    @Delete
    suspend fun deleteGoal(goalEntity: Goal)
}