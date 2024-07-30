package com.example.tsumiageapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsumiageapp.data.model.Goal
import com.example.tsumiageapp.data.model.GoalTypeEnum
import com.example.tsumiageapp.data.model.Task
import com.example.tsumiageapp.ui.goal.GoalRepository
import com.example.tsumiageapp.ui.task.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val goalRepository: GoalRepository
): ViewModel() {
    private val _todayTasksUiState = MutableStateFlow<List<Task>>(emptyList())
    val todayTasksUiState: StateFlow<List<Task>> = _todayTasksUiState.asStateFlow()

    private val _theseDaysTasksState = MutableStateFlow<List<Task>>(emptyList())
    val theseDaysTasksState: StateFlow<List<Task>> = _theseDaysTasksState.asStateFlow()

    private val _todayGoalListState = MutableStateFlow<List<Goal>>(emptyList())
    val todayGoalListState: StateFlow<List<Goal>> = _todayGoalListState.asStateFlow()

    //未達成リスト
    private val _todayIncompleteGoalList = MutableStateFlow<List<Goal>>(emptyList())
    val todayIncompleteGoalList: StateFlow<List<Goal>> = _todayIncompleteGoalList.asStateFlow()

    //達成リスト
    private val _todayCompleteGoalList = MutableStateFlow<List<Goal>>(emptyList())
    val todayCompleteGoalList: StateFlow<List<Goal>> = _todayCompleteGoalList.asStateFlow()

    private val today = LocalDate.now()



    init {
        getTodayTasks()
        getTheseDaysTasks()
        getTodayGoalList()
    }

    fun getTodayTasks() {
        viewModelScope.launch {
            taskRepository.getTasksByDate(today).collect{
                _todayTasksUiState.value = it
            }
        }
    }

    private fun getTheseDaysTasks() {
        val yesterday = today.minusDays(1)
        val dayBeforeYesterday = today.minusDays(2)

        viewModelScope.launch {
            taskRepository.getTasksBetweenDates(dayBeforeYesterday,yesterday).collect{
                _theseDaysTasksState.value = it
            }
        }
    }


    //今日の目標リスト作成
    private fun getTodayGoalList() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val todayDayGoalType = GoalTypeEnum.entries.firstOrNull {
            it.name == today.dayOfWeek.toString()
        }

        viewModelScope.launch {
            merge(
                //今日の目標
                getGoalListByTypeAndDate(GoalTypeEnum.TODAY,today.toString()),
                //昨日登録した今日の目標
                getGoalListByTypeAndDate(GoalTypeEnum.TOMORROW,yesterday.toString()),
                //毎日の目標
                goalRepository.getGoalByType(GoalTypeEnum.EVERYDAY.name),
                //その曜日の目標
                goalRepository.getGoalByType(todayDayGoalType?.name ?: "")
            ).collect{ newGoalList ->
                val combinesGoalList = (_todayGoalListState.value + newGoalList).distinctBy { it.id }
                _todayGoalListState.value = combinesGoalList
            }
        }
    }

    private fun getGoalListByTypeAndDate(goalType: GoalTypeEnum,date: String): Flow<List<Goal>> {
        return goalRepository.getGoalByType(goalType.name).map {goalList ->
            goalList.filter {
                it.addDate == date
            }
        }
    }


    fun getIncompleteGoals() {
        _todayIncompleteGoalList.value = _todayGoalListState.value.filter { goal ->
            _todayTasksUiState.value.none {task ->
                task.title == goal.title &&
                        task.categoryType == goal.categoryType &&
                        task.time!! >= goal.time
            }
        }

        _todayCompleteGoalList.value = _todayGoalListState.value.filter{ goal ->
            !_todayIncompleteGoalList.value.contains(goal)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}