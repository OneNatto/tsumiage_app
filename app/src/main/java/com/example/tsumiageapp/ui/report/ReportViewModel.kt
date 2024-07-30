package com.example.tsumiageapp.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsumiageapp.data.model.Category
import com.example.tsumiageapp.data.model.Task
import com.example.tsumiageapp.ui.common.functions.changeMinuteToHourAndMinute
import com.example.tsumiageapp.ui.task.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val taskRepository: TaskRepository
): ViewModel() {

    private val today = LocalDate.now()
    private val dayFormatter = DateTimeFormatter.ofPattern("M月d日")

    private val _reportTermText = MutableStateFlow(setTermTextByDate(today))
    val reportTermText:StateFlow<String> = _reportTermText.asStateFlow()

    private val _reportTasksState = MutableStateFlow<Map<String,List<Task>>?>(null)
    val reportTasksState: StateFlow<Map<String,List<Task>>?> = _reportTasksState.asStateFlow()

    //日の選択リスト
    private val _selectDateList = MutableStateFlow<List<LocalDate>>(emptyList())
    val selectDateList:StateFlow<List<LocalDate>> = _selectDateList.asStateFlow()

    //週の日付リスト
    private val _weekDatesList = MutableStateFlow(getWeekDatesList(today.with(DayOfWeek.MONDAY)))
    val weekDatesList: StateFlow<List<LocalDate>> = _weekDatesList.asStateFlow()

    //月の日付リスト
    private val _monthDatesList = MutableStateFlow(getMonthDatesList(today.withDayOfMonth(1)))
    val monthDatesList: StateFlow<List<LocalDate>> = _monthDatesList.asStateFlow()

    private val currentSelectMaxDate = MutableStateFlow(30)

    //合計時間
    private val _taskTotalWorkTime = MutableStateFlow("")
    val taskTotalWorkTime: StateFlow<String> = _taskTotalWorkTime.asStateFlow()

    //平均時間
    private val _taskAverageWorkTime = MutableStateFlow("")
    val taskAverageWorkTime: StateFlow<String> = _taskAverageWorkTime.asStateFlow()

    //合計ツミアゲ
    private val _taskTotalBoxNumber = MutableStateFlow<Int>(0)
    val taskTotalBoxNumber: StateFlow<Int> = _taskTotalBoxNumber.asStateFlow()

    //平均ツミアゲ
    private val _taskAverageBoxNumber = MutableStateFlow<Int>(0)
    val taskAverageBoxNumber: StateFlow<Int> = _taskAverageBoxNumber.asStateFlow()

    //ツミアゲ最多日
    private val _topBoxNumDate = MutableStateFlow<String>("")
    val topBoxNumDate: StateFlow<String> = _topBoxNumDate.asStateFlow()


    private val _dateState = MutableStateFlow<LocalDate>(today)
    val dateState:StateFlow<LocalDate> = _dateState.asStateFlow()

    private val startDateOfWeekState = MutableStateFlow<LocalDate>(today.with(DayOfWeek.MONDAY))
    private val startDateOfMonthState = MutableStateFlow<LocalDate>(today.withDayOfMonth(1))

    init {
        _reportTermText.value = setTermTextByDate(today)
        getTasksByDate(_dateState.value)

        viewModelScope.launch {
            startDateOfWeekState.collect{startDate ->
                _weekDatesList.value = getWeekDatesList(startDate)
            }
        }

        viewModelScope.launch {
            startDateOfMonthState.collect{ startDate ->
                _monthDatesList.value = getMonthDatesList(startDate)
            }
        }
        setSelectDateList()
    }

    //タブを押したときに
    fun setTermText(screen: Screen) {
        resetState()

        when(screen) {
            Screen.DAILY -> {
                _reportTermText.value = setTermTextByDate(_dateState.value)
                getTasksByDate(_dateState.value)
            }
            Screen.WEEKLY -> {
                _reportTermText.value = setTermTextByWeek(startDateOfWeekState.value)
                val startDate = startDateOfWeekState.value
                val endDate = startDateOfWeekState.value.plusDays(6)
                getTasksBetweenDates(startDate,endDate)
            }
            Screen.MONTHLY -> {
                _reportTermText.value = setTermTextByMonth(startDateOfMonthState.value)
                val startDate = startDateOfMonthState.value
                val endDate = startDateOfMonthState.value.plusMonths(1).minusDays(1)
                getTasksBetweenDates(startDate,endDate)
            }
        }
    }

    //前に戻るボタン
    fun onClickedPrevButton(screen: Screen) {
        when(screen) {
            Screen.DAILY -> {
                _dateState.value = _dateState.value.minusDays(1)
                _reportTermText.value = setTermTextByDate(_dateState.value)
                getTasksByDate(_dateState.value)
            }
            Screen.WEEKLY -> {
                startDateOfWeekState.value = startDateOfWeekState.value.minusWeeks(1)
                _reportTermText.value = setTermTextByWeek(startDateOfWeekState.value)

                val startDate = startDateOfWeekState.value
                val endDate = startDateOfWeekState.value.plusDays(6)
                getTasksBetweenDates(startDate,endDate)
            }
            Screen.MONTHLY -> {
                startDateOfMonthState.value = startDateOfMonthState.value.minusMonths(1)
                _reportTermText.value = setTermTextByMonth(startDateOfMonthState.value)

                val startDate = startDateOfMonthState.value
                val endDate = startDateOfMonthState.value.plusMonths(1).minusDays(1)
                getTasksBetweenDates(startDate,endDate)
            }
        }
    }

    //前に戻るボタン
    fun onClickedNextButton(screen: Screen) {
        when(screen) {
            Screen.DAILY -> {
                if(_dateState.value != today) {
                    _dateState.value = _dateState.value.plusDays(1)
                    _reportTermText.value = setTermTextByDate(_dateState.value)

                    getTasksByDate(_dateState.value)
                }
            }
            Screen.WEEKLY -> {
                if(startDateOfWeekState.value != today.with(DayOfWeek.MONDAY)) {
                    startDateOfWeekState.value = startDateOfWeekState.value.plusWeeks(1)
                    _reportTermText.value = setTermTextByWeek(startDateOfWeekState.value)

                    val startDate = startDateOfWeekState.value
                    val endDate = startDateOfWeekState.value.plusDays(6)
                    getTasksBetweenDates(startDate,endDate)
                }
            }
            Screen.MONTHLY -> {
                if(startDateOfMonthState.value != today.withDayOfMonth(1)) {
                    startDateOfMonthState.value = startDateOfMonthState.value.plusMonths(1)
                    _reportTermText.value = setTermTextByMonth(startDateOfMonthState.value)

                    val startDate = startDateOfMonthState.value
                    val endDate = startDateOfMonthState.value.plusMonths(1).minusDays(1)
                    getTasksBetweenDates(startDate,endDate)
                }
            }
        }
    }

    fun setDateByPicker(date: LocalDate) {
        _dateState.value = date
        _reportTermText.value = setTermTextByDate(_dateState.value)
        getTasksByDate(_dateState.value)
    }

    private fun setTermTextByDate(date: LocalDate): String {
        val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.JAPANESE)
        val formattedTodayDate = date.format(dayFormatter)

        return "$formattedTodayDate（$dayOfWeek）"
    }

    private fun setTermTextByWeek(statDayOfWeek: LocalDate): String {
        val endOfWeek = statDayOfWeek.plusDays(6)

        val formattedStartOfWeek = statDayOfWeek.format(dayFormatter)
        val formattedEndOfWeek = endOfWeek.format(dayFormatter)

        return "$formattedStartOfWeek 〜 $formattedEndOfWeek"
    }

    private fun setTermTextByMonth(startDateOfMonth: LocalDate): String {
        val thisYear = startDateOfMonth.year
        val thisMonth = startDateOfMonth.monthValue
        return "${thisYear}年${thisMonth}月"
    }

    private fun resetState() {
        if(_dateState.value != today) {
            _dateState.value = today
        }
        if(startDateOfWeekState.value != today.with(DayOfWeek.MONDAY)) {
            startDateOfWeekState.value = today.with(DayOfWeek.MONDAY)
        }
        if(startDateOfMonthState.value != today.withDayOfMonth(1)) {
            startDateOfMonthState.value = today.withDayOfMonth(1)
        }
    }

    private fun setSelectDateList() {
        (currentSelectMaxDate.value - 30..< currentSelectMaxDate.value).forEach {
            val date = today.minusDays(it.toLong())
            _selectDateList.value += date
        }
    }

    fun getMoreSelectDateList() {
        currentSelectMaxDate.value += 30
        setSelectDateList()
    }

    fun getTasksByDate(date: LocalDate) {
        viewModelScope.launch {

            taskRepository.getTasksByDate(date).collect{taskList ->
                if(taskList.isNotEmpty()) {
                    val totalTimeOfTasks = getTotalWorkTimeOfTasks(taskList)

                    _reportTasksState.value = groupTasksByDate(taskList)
                    _taskTotalWorkTime.value = changeMinuteToHourAndMinute(totalTimeOfTasks)
                    _taskTotalBoxNumber.value = taskList.size
                } else {
                    _reportTasksState.value = emptyMap()
                    _taskTotalWorkTime.value = "0"
                    _taskTotalBoxNumber.value = 0
                }
            }
        }
    }

    fun getTasksBetweenDates(startDate: LocalDate, endDate: LocalDate) {

        val totalDates = ChronoUnit.DAYS.between(startDate,endDate) + 1
        viewModelScope.launch {
            taskRepository.getTasksBetweenDates(startDate, endDate).collect{ taskList ->

                if(taskList.isNotEmpty()) {
                    val taskGroup = groupTasksByDate(taskList)
                    val dateWithMostTask :LocalDate? = getDateWithMostTasks(taskGroup)
                    val formattedDate = dateWithMostTask?.format(dayFormatter)

                    val totalTimeOfTasks = getTotalWorkTimeOfTasks(taskList)
                    val averageTimeOfTasks = getAverageWorkTimeOfTasks(totalDates.toInt(),totalTimeOfTasks)

                    _reportTasksState.value = taskGroup
                    _taskTotalWorkTime.value = changeMinuteToHourAndMinute(totalTimeOfTasks)
                    _taskAverageWorkTime.value = changeMinuteToHourAndMinute(averageTimeOfTasks)
                    _taskTotalBoxNumber.value = taskList.size
                    _taskAverageBoxNumber.value = taskList.size.div(taskGroup.size)
                    _topBoxNumDate.value = formattedDate ?: ""
                } else {
                    _reportTasksState.value = emptyMap()
                    _taskTotalWorkTime.value = "0"
                    _taskAverageWorkTime.value = "0"
                    _taskTotalBoxNumber.value = 0
                    _taskAverageBoxNumber.value = 0
                    _topBoxNumDate.value = "なし"
                }
            }
        }
    }

    //週の日付リストを取得
    private fun getWeekDatesList(startDateOfWeek: LocalDate) : List<LocalDate> {
        return (0..6).map {
            startDateOfWeek.plusDays(it.toLong())
        }
    }

    //月の日付リストを取得
    private fun getMonthDatesList(startDateOfMonth: LocalDate) : List<LocalDate> {
        val lastDay = startDateOfMonth.lengthOfMonth()
        return (1..lastDay).map { day -> startDateOfMonth.withDayOfMonth(day) }
    }

    //週別・月別のグループ分け
    private fun groupTasksByDate(tasks: List<Task>) :Map<String,List<Task>>  {
        return tasks.groupBy { it.date }
    }

    //全タスク合計時間
    private fun getTotalWorkTimeOfTasks(tasks:List<Task>) : Int {
        var totalWorkTime = 0
        tasks.forEach { task ->
            if(task.time != null) {
                totalWorkTime += task.time
            }
        }
        return totalWorkTime
    }

    //全タスク平均時間
    private fun getAverageWorkTimeOfTasks(numberOfDate: Int,totalWorkTime: Int) : Int {
        return totalWorkTime/numberOfDate
    }

    //ツミアゲ最多日
    private fun getDateWithMostTasks(taskGroup: Map<String,List<Task>>) : LocalDate? {
        val dateWithMostTask: String? = taskGroup.entries.maxByOrNull { entry ->
            entry.value.size
        }?.key

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val localDate: LocalDate? = dateWithMostTask?.let { LocalDate.parse(it,formatter) }

        return localDate
    }

    fun formatDateString(inputDate: String) : String {
        val date = LocalDate.parse(inputDate)

        val month = date.month.value
        val dayOfMonth = date.dayOfMonth

        val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.JAPAN)

        return "${month}月${dayOfMonth}日(${dayOfWeek})"
    }

    fun getTaskTotalTime(taskList: List<Task>) : Int {
        var taskTotalTime = 0
        taskList.forEach { task ->
            if(task.time != null) {
                taskTotalTime = taskTotalTime.plus(task.time)
            }
            println(taskTotalTime)
        }
        return taskTotalTime
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}