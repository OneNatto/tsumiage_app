package com.example.tsumiageapp.ui.task

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsumiageapp.data.model.*
import com.example.tsumiageapp.ui.category.CategoryRepository
import com.example.tsumiageapp.ui.common.functions.changeHourAndMinuteToMinute
import com.example.tsumiageapp.ui.goal.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val goalRepository: GoalRepository
): ViewModel() {
    //時間選択State
    private val _selectedHourState = MutableStateFlow<Int?>(null)
    val selectedHourState: StateFlow<Int?> = _selectedHourState.asStateFlow()

    //分数選択State
    private val _selectedMinuteState = MutableStateFlow<Int?>(null)
    val selectedMinuteState: StateFlow<Int?> = _selectedMinuteState.asStateFlow()

    //カレンダー
    private val _selectedDate = MutableStateFlow<LocalDate?>(getTodayDate())
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    //タスク
    private val _taskNameUiState = MutableStateFlow("")
    val taskNameUiState: StateFlow<String> = _taskNameUiState.asStateFlow()

    //今日のタスク
    private val _todayTaskList = MutableStateFlow<List<Task>>(emptyList())

    //今日の目標
    private val _todayGoalList = MutableStateFlow<List<Goal>>(emptyList())

    //達成・未達成目標マップ
    private val _todayGoalMap = MutableStateFlow<List<Map<Boolean,Goal>>>(emptyList())
    val todayGoalMap: StateFlow<List<Map<Boolean,Goal>>> = _todayGoalMap.asStateFlow()

    //新カテゴリー名
    private val _categoryNameUiState = MutableStateFlow<String>("")
    val categoryNameUiState :StateFlow<String> = _categoryNameUiState.asStateFlow()

    //新カテゴリータイプ
    private val _newCategoryTypeState = MutableStateFlow<CategoryTypeEnum?>(null)
    val newCategoryTypeState:StateFlow<CategoryTypeEnum?> = _newCategoryTypeState.asStateFlow()

    //選択カテゴリー
    private val _selectedCategoryState = MutableStateFlow<Category>(Category())
    val selectedCategoryState: StateFlow<Category> = _selectedCategoryState.asStateFlow()

    //カテゴリーリスト
    private val _categoryListUiState = MutableStateFlow<List<Category>>(emptyList())
    val categoryListUiState :StateFlow<List<Category>> = _categoryListUiState.asStateFlow()

    //空欄がないかチェック
    val isFormValidate: StateFlow<Boolean> = combine(
        _taskNameUiState,_selectedHourState,_selectedMinuteState,
    ) { name,hour,minute ->
        name != "" && (hour!= 0 || minute != 0)
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        getCategories()
        getTodayGoalList()
        getTodayTaskList()
    }

    //登録済みカテゴリーの取得
    private fun getTodayTaskList() {
        viewModelScope.launch {
            taskRepository.getTasksByDate(LocalDate.now()).collect { taskList ->
                _todayTaskList.value = taskList
            }
        }
    }

    //登録済みカテゴリーの取得
    private fun getCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories().collect {
                _categoryListUiState.value = it
            }
        }
    }

    //今日の日付を取得
    private fun getTodayDate(): LocalDate {
        val zoneId = ZoneId.of("Asia/Tokyo")
        val todayDate = LocalDate.now(zoneId)
        return todayDate
    }

    //タスク名
    fun setTaskTitle(value: String) {
        _taskNameUiState.value = value
    }

    //カテゴリー入力
    fun setCategoryName(value: String) {
        _categoryNameUiState.value = value
    }

    //カテゴリー入力取り消し
    fun clearCategoryName() {
        _categoryNameUiState.value = ""
        _newCategoryTypeState.value = null
    }

    fun selectCategory(category: Category) {
        _selectedCategoryState.value = category
    }

    //カテゴリー追加
    fun addCategory() {
        viewModelScope.launch {
            if(_categoryNameUiState.value != "") {
                val newCategory = Category(
                    name = _categoryNameUiState.value,
                    categoryType = if(_newCategoryTypeState.value != null) _newCategoryTypeState.value!!.name else ""
                )
                categoryRepository.addCategory(newCategory)
                _categoryNameUiState.value = ""
            }
            if(_newCategoryTypeState.value != null) {
                _newCategoryTypeState.value = null
            }
        }
    }

    fun setNewCategoryType(type: CategoryTypeEnum) {
        _newCategoryTypeState.value = type
    }

    //時間選択
    fun selectHour(hour: Int) {
        _selectedHourState.value = hour
    }

    //分数選
    fun selectMinute(minute: Int) {
        _selectedMinuteState.value = minute
    }



    //日付選択ダイアログ
    fun showDatePickerDialog(context: Context) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val japaneseLocale = java.util.Locale("ja", "JP")
        java.util.Locale.setDefault(japaneseLocale)
        val config = context.resources.configuration
        config.setLocale(japaneseLocale)
        context.createConfigurationContext(config)

        val datePickerDialog = DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
            _selectedDate.value = selectedDate
        }, year, month, day)

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "キャンセル"){ dialog, _ ->
            dialog.dismiss()
        }

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "選択") { dialog, _ ->
            datePickerDialog.onClick(datePickerDialog, DialogInterface.BUTTON_POSITIVE)
        }

        datePickerDialog.show()
    }

    //ツミアゲる
    fun addTask() {
        val dateTime = _selectedDate.value.toString()

        val minute = changeHourAndMinuteToMinute(_selectedHourState.value,_selectedMinuteState.value)

        val newTask = Task(
            title = _taskNameUiState.value,
            categoryType = _selectedCategoryState.value.categoryType,
            categoryName = _selectedCategoryState.value.name,
            time = minute,
            date = dateTime
        )

        viewModelScope.launch {
            taskRepository.addTask(newTask)
            _taskNameUiState.value = ""
            _selectedCategoryState.value = Category()
            _selectedHourState.value = null
            _selectedMinuteState.value = null
        }
    }

    private fun getTodayGoalList() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        var goalListOfTypeToday: List<Goal>
        var goalListOfTypeTomorrow: List<Goal>


        val todayDayGoalType = GoalTypeEnum.entries.firstOrNull {
            it.name == today.dayOfWeek.toString()
        }

        viewModelScope.launch {
            goalRepository.getGoalByType(GoalTypeEnum.TODAY.name).collect{ goalList ->
                goalListOfTypeToday = goalList.filter { goal ->
                    goal.addDate == today.toString()
                }

                val newGoals = goalListOfTypeToday.filterNot {goal ->
                    _todayGoalList.value.contains(goal)
                }
                _todayGoalList.value += newGoals

            }
        }
        viewModelScope.launch {
            goalRepository.getGoalByType(GoalTypeEnum.TOMORROW.name).collect{ goalList ->
                goalListOfTypeTomorrow = goalList.filter { goal ->
                    goal.addDate == yesterday.toString()
                }
                val newGoals = goalListOfTypeTomorrow.filterNot {goal ->
                    _todayGoalList.value.contains(goal)
                }
                _todayGoalList.value += newGoals
            }
        }

        viewModelScope.launch {
            goalRepository.getGoalByType(todayDayGoalType.toString()).collect{ goalList ->
                val newGoal = goalList.filterNot { goal ->
                    _todayGoalList.value.contains(goal)
                }
                _todayGoalList.value += newGoal
            }
        }

        viewModelScope.launch {
            goalRepository.getGoalByType(GoalTypeEnum.EVERYDAY.name).collect{ goalList ->
                val newGoal = goalList.filterNot { goal ->
                    _todayGoalList.value.contains(goal)
                }
                _todayGoalList.value += newGoal
            }
        }
    }

    fun addTaskFromGoal(goal: Goal) {
        val newTask = Task(
            title = goal.title,
            categoryType = goal.categoryType,
            categoryName = goal.categoryName,
            time = goal.time,
            date = LocalDate.now().toString()
        )
        viewModelScope.launch {
            taskRepository.addTask(newTask)
        }
    }

    fun getComparedTaskAndGoal() {
        _todayGoalMap.value = _todayGoalList.value.map { goal ->
           val isDone = _todayTaskList.value.any { task ->
               task.title == goal.title && task.time!! >= goal.time && task.categoryName == goal.categoryName
           }
           mapOf(isDone to goal)
       }
    }
}