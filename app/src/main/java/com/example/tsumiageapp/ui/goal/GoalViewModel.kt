package com.example.tsumiageapp.ui.goal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsumiageapp.data.model.Category
import com.example.tsumiageapp.data.model.CategoryTypeEnum
import com.example.tsumiageapp.data.model.Goal
import com.example.tsumiageapp.data.model.GoalTypeEnum
import com.example.tsumiageapp.ui.category.CategoryRepository
import com.example.tsumiageapp.ui.common.functions.changeHourAndMinuteToMinute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val categoryRepository: CategoryRepository
): ViewModel() {

    //今日の目標リスト
    private val _todayGoalList = MutableStateFlow<List<Goal>>(emptyList())
    val todayGoalList: StateFlow<List<Goal>> = _todayGoalList.asStateFlow()

    //明日の目標リスト
    private val _tomorrowGoalList = MutableStateFlow<List<Goal>>(emptyList())
    val tomorrowGoalList: StateFlow<List<Goal>> = _tomorrowGoalList.asStateFlow()

    //曜日ごとの目標リスト
    private val _goalListByDay = MutableStateFlow(emptyList<Goal>())
    val goalListByDay: StateFlow<List<Goal>> = _goalListByDay.asStateFlow()

    //目標タイトル
    private val _goalNameState = MutableStateFlow("")
    val goalNameState: StateFlow<String> = _goalNameState.asStateFlow()

    //目標時間 | 時間
    private val _selectedGoalHourState = MutableStateFlow<Int?>(null)
    val selectedGoalHourState: StateFlow<Int?> = _selectedGoalHourState.asStateFlow()

    //目標時間 | 分
    private val _selectedGoalMinuteState = MutableStateFlow<Int?>(null)
    val selectedGoalMinuteState: StateFlow<Int?> = _selectedGoalMinuteState.asStateFlow()

    //登録カテゴリー
    private val _categoryListState = MutableStateFlow<List<Category>>(emptyList())
    val categoryListState: StateFlow<List<Category>> = _categoryListState.asStateFlow()

    //選択カテゴリー
    private val _selectedGoalCategory = MutableStateFlow(Category())
    val selectedGoalCategory: StateFlow<Category> = _selectedGoalCategory.asStateFlow()

    //目標タイプ
    private val _selectedGoalType = MutableStateFlow(GoalTypeEnum.TODAY)
    val selectedGoalType:StateFlow<GoalTypeEnum> = _selectedGoalType.asStateFlow()

    //新カテゴリー名前
    private val _newCategoryNameState = MutableStateFlow("")
    val newCategoryNameState: StateFlow<String> = _newCategoryNameState.asStateFlow()

    //新カテゴリータイプ
    private val _newCategoryTypeState = MutableStateFlow<CategoryTypeEnum?>(null)
    val newCategoryTypeState: StateFlow<CategoryTypeEnum?> = _newCategoryTypeState.asStateFlow()

    val isFormValidate: StateFlow<Boolean> = combine(
        _goalNameState,_selectedGoalHourState,_selectedGoalMinuteState,
    ) { name,hour,minute ->
        name != "" && (hour != 0 || minute != 0)
    }.stateIn(viewModelScope, SharingStarted.Lazily,false)


    init {
        getCategories()
        getTodayGoalList()
        getTomorrowGoalList()
        setGoalListByDay(GoalTypeEnum.EVERYDAY)
    }

    fun deleteGoal(goal:Goal) {
        viewModelScope.launch {
            goalRepository.deleteGoal(goal)
            _todayGoalList.value = _todayGoalList.value.filterNot { it.id == goal.id }
            _tomorrowGoalList.value = _tomorrowGoalList.value.filterNot { it.id == goal.id }
        }
    }

    fun setGoalTitle(title: String) {
        _goalNameState.value = title
    }

    fun setGoalHour(hour: Int) {
        _selectedGoalHourState.value = hour
    }

    fun setGoalMinute(minute: Int) {
        _selectedGoalMinuteState.value = minute
    }

    fun setGoalCategory(selectedCategory: Category) {
        _selectedGoalCategory.value = selectedCategory
    }

    fun setGoalType(selectedGoalType: GoalTypeEnum) {
        _selectedGoalType.value = selectedGoalType
    }

    fun addGoal() {

        val goalTime = changeHourAndMinuteToMinute(_selectedGoalHourState.value,_selectedGoalMinuteState.value)

        val goal = Goal(
            title = _goalNameState.value,
            time = goalTime,
            categoryType = _selectedGoalCategory.value.categoryType,
            categoryName = _selectedGoalCategory.value.name,
            goalType = _selectedGoalType.value.name,
            addDate = LocalDate.now().toString(),
            isDone = false
        )

        viewModelScope.launch{
            goalRepository.addGoal(goal)
            _goalNameState.value = ""
            _selectedGoalHourState.value = 0
            _selectedGoalMinuteState.value = 0
            _selectedGoalCategory.value = Category()
            _selectedGoalType.value = GoalTypeEnum.TODAY
        }
    }

    //今日の目標リスト作成
    fun getTodayGoalList() {
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

                val combinesGoalList = (_todayGoalList.value + newGoalList).distinctBy { it.id }

                _todayGoalList.value = combinesGoalList
            }
        }
    }

    //明日の目標リスト作成
    private fun getTomorrowGoalList() {
        val today = LocalDate.now()
        val tomorrowDayGoalType = GoalTypeEnum.entries.firstOrNull {
            it.name == today.plusDays(1).dayOfWeek.toString()
        }

        viewModelScope.launch {
            merge(
                //今日登録した明日の目標
                getGoalListByTypeAndDate(GoalTypeEnum.TOMORROW,today.toString()),
                //毎日の目標
                goalRepository.getGoalByType(GoalTypeEnum.EVERYDAY.name),
                //その曜日の目標
                goalRepository.getGoalByType(tomorrowDayGoalType?.name ?: "")
            ).collect{newGoalList ->
                val combineGoalList = (_tomorrowGoalList.value + newGoalList).distinctBy { it.id }
                _tomorrowGoalList.value = combineGoalList
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

    //タブの切り替えで曜日ごとの目標リストを作成
    fun setGoalListByDay(goalTypeEnum: GoalTypeEnum) {
        viewModelScope.launch {
            goalRepository.getGoalByType(goalTypeEnum.name).collect{ goalList ->
                _goalListByDay.value = goalList
            }
        }
    }

    //新カテゴリー入力
    fun setNewCategoryName(value: String) {
        _newCategoryNameState.value = value
    }

    //新カテゴリーのタイプ選択
    fun setNewCategoryName(newCategoryType: CategoryTypeEnum) {
        _newCategoryTypeState.value = newCategoryType
    }

    //カテゴリー追加キャンセル
    fun clearNewCategory() {
        _newCategoryNameState.value = ""
        _newCategoryTypeState.value = null
    }

    //新カテゴリーの追加
    fun addNewCategory() {
        if(_newCategoryNameState.value != "") {
            val newCategory = Category(
                categoryType = _newCategoryTypeState.value!!.name,
                name = _newCategoryNameState.value
            )

            viewModelScope.launch {
                categoryRepository.addCategory(newCategory)
            }

            _newCategoryNameState.value = ""

            if(_newCategoryTypeState.value != null) {
                _newCategoryTypeState.value = null
            }
        }
    }

    //登録カテゴリーの取得
    private fun getCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories().collect{ categoryList ->
                _categoryListState.value = categoryList
            }
        }
    }
}