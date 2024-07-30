package com.example.tsumiageapp.ui.report

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tsumiageapp.data.model.Task
import com.example.tsumiageapp.ui.common.components.BoxNumberGrid
import com.example.tsumiageapp.ui.common.components.TaskBox
import com.example.tsumiageapp.ui.common.components.TaskModalBottomSheet
import com.example.tsumiageapp.ui.common.enums.BoxTypeEnum
import com.example.tsumiageapp.ui.common.functions.*
import java.time.LocalDate

enum class Screen(val title: String) {
    DAILY("日別"),WEEKLY("週別"),MONTHLY("月別")
}

@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: ReportViewModel = hiltViewModel()
    var selectedTab: Screen by remember { mutableStateOf(Screen.DAILY) }

    LazyColumn(
        modifier = modifier
    ) {
        item {
            DataTypeTab(
                selectedTab = selectedTab,
                tabClicked = {
                    selectedTab = Screen.entries[it]
                    viewModel.setTermText(Screen.entries[it])
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            //レポートメイン画面
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                TaskReport(
                    viewModel = viewModel,
                    screenType = selectedTab,
                    onClickedPrevButton = { viewModel.onClickedPrevButton(selectedTab) },
                    onClickedNextButton = { viewModel.onClickedNextButton(selectedTab) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )
            }
        }
    }
}

@Composable
fun DataTypeTab(
    selectedTab: Screen,
    tabClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = modifier
    ) {
        //タブメニュー
        TabRow(
            selectedTabIndex = selectedTab.ordinal
        ) {
            Screen.entries.map { it.title }.forEachIndexed { index, name ->
                Tab(
                    text = {
                        Text(
                            name,
                            color = if (selectedTab.ordinal == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        ) },
                    selected = selectedTab.ordinal == index,
                    onClick = { tabClicked(index) },
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.background
                    )
                )
            }
        }
    }
}

@Composable
fun TaskReport(
    viewModel: ReportViewModel,
    screenType: Screen,
    onClickedPrevButton: () -> Unit,
    onClickedNextButton: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reportTermText by viewModel.reportTermText.collectAsState()
    val reportTasksState by viewModel.reportTasksState.collectAsState()

    val weekDatesList by viewModel.weekDatesList.collectAsState()
    val monthDatesList by viewModel.monthDatesList.collectAsState()

    val selectDateList by viewModel.selectDateList.collectAsState()

    val taskTotalWorkTime by viewModel.taskTotalWorkTime.collectAsState()
    val taskAverageWorkTime by viewModel.taskAverageWorkTime.collectAsState()
    val taskTotalBoxNumber by viewModel.taskTotalBoxNumber.collectAsState()
    val taskAverageBoxNumber by viewModel.taskAverageBoxNumber.collectAsState()
    val topBoxNumDate by viewModel.topBoxNumDate.collectAsState()

    val dateState by viewModel.dateState.collectAsState()
    val taskListByDate = reportTasksState?.get(dateState.toString())

    var showModalBottomSheet by remember { mutableStateOf(false) }
    var selectedTask: Task? by remember { mutableStateOf(null) }
    var showDateSelectDialog by remember{ mutableStateOf(false) }

    Column(modifier = modifier) {
        if(screenType != Screen.DAILY) {
            WeekAndMonthTermRow(
                reportTermText,
                onClickedPrevButton,
                onClickedNextButton,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        } else {
            DateTermRow(
                reportTermText = reportTermText,
                modifier = Modifier
                    .fillMaxWidth(0.7F)
                    .clickable {
                        showDateSelectDialog = !showDateSelectDialog
                    }
                    .padding(vertical = 12.dp)
            )
        }
        //データチャート
        if(reportTasksState != null) {
            //日別データ
            when(screenType) {
                Screen.DAILY -> {
                    var taskListSize = 0
                    reportTasksState!!.entries.forEach{
                        taskListSize = it.value.size
                    }

                    val oneDateDataTaskListHeight : Dp = getTaskListContentHeightByBoxNumber(taskListSize,90).dp

                    val oneDateTaskScrollState = rememberScrollState()
                    DailyReportBox(
                        taskList = taskListByDate,
                        taskListSize = taskListSize,
                        boxHeight = oneDateDataTaskListHeight,
                        taskClicked = {
                            selectedTask = it
                            showModalBottomSheet = !showModalBottomSheet
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(450.dp)
                            .verticalScroll(oneDateTaskScrollState)
                    )
                    LaunchedEffect(reportTasksState) {
                        oneDateTaskScrollState.animateScrollTo(oneDateTaskScrollState.maxValue)
                    }
                }
                Screen.WEEKLY -> {
                    val taskListSize = reportTasksState!!.entries.maxByOrNull {
                        it.value.size
                    }?.value?.size
                    var taskTotalTime = 0
                    reportTasksState!!.entries.forEach {
                        val currentTaskTotalTime = viewModel.getTaskTotalTime(it.value)

                        if(taskTotalTime < currentTaskTotalTime) {
                            taskTotalTime = currentTaskTotalTime
                        }
                    }
                    var weekDataTaskListHeight = 360.dp
                    if(taskListSize != null) {
                        weekDataTaskListHeight = getTaskListContentHeightByBoxNumber(taskListSize,60).dp
                    }
                    val weekScrollState = rememberScrollState()

                    WeeklyReportBox(
                        viewModel = viewModel,
                        reportTasksState = reportTasksState!!,
                        weekDatesList = weekDatesList,
                        taskListSize = taskListSize ?: 0,
                        weekDataTaskListHeight = weekDataTaskListHeight,
                        taskClicked = {
                            selectedTask = it
                            showModalBottomSheet = !showModalBottomSheet
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .verticalScroll(weekScrollState)
                    )
                    LaunchedEffect(reportTasksState) {
                        weekScrollState.animateScrollTo(weekScrollState.maxValue)
                    }
                }
                Screen.MONTHLY -> {
                    val taskListSize = reportTasksState!!.entries.maxByOrNull {
                        it.value.size
                    }?.value?.size
                    var taskTotalTime = 0
                    reportTasksState!!.entries.forEach {
                        val currentTaskTotalTime = viewModel.getTaskTotalTime(it.value)

                        if(taskTotalTime < currentTaskTotalTime) {
                            taskTotalTime = currentTaskTotalTime
                        }
                    }
                    var monthDataTaskListHeight = 360.dp
                    if(taskListSize != null) {
                        monthDataTaskListHeight = getTaskListContentHeightByBoxNumber(taskListSize,60).dp
                    }
                    val monthScrollState = rememberScrollState()

                    MonthlyReportBox(
                        viewModel = viewModel,
                        reportTasksState = reportTasksState!!,
                        monthDatesList = monthDatesList,
                        taskListSize = taskListSize ?: 0,
                        monthDataTaskListHeight = monthDataTaskListHeight,
                        taskClicked = {
                            selectedTask = it
                            showModalBottomSheet = !showModalBottomSheet
                        },
                        modifier = Modifier.fillMaxWidth()
                            .height(450.dp)
                            .verticalScroll(monthScrollState)
                    )
                    LaunchedEffect(reportTasksState) {
                        monthScrollState.animateScrollTo(monthScrollState.maxValue)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        //統計テーブル
        if(screenType == Screen.DAILY) {
            DailyStaticContent(
                taskTotalWorkTime,
                taskTotalBoxNumber,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        } else {
            WeeklyAndMonthlyStaticContent(
                taskTotalWorkTime,
                taskAverageWorkTime,
                taskTotalBoxNumber,
                taskAverageBoxNumber,
                topBoxNumDate,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
        if(showModalBottomSheet) {
            selectedTask?.let {
                TaskModalBottomSheet(
                    task = it,
                    onDismissRequest = {
                        selectedTask = null
                        showModalBottomSheet = false
                    },
                    onDeleteClicked = {
                        viewModel.deleteTask(it)
                        selectedTask = null
                        showModalBottomSheet = false
                    }
                )
            }
        }
        if(showDateSelectDialog) {
            DateSelectDialog(
                selectDateList = selectDateList,
                dateClicked = {
                    viewModel.setDateByPicker(it)
                    showDateSelectDialog = !showDateSelectDialog
                },
                onDismissRequest = {
                    showDateSelectDialog = !showDateSelectDialog
                },
                moreButtonClicked = { viewModel.getMoreSelectDateList() }
            )
        }
    }
}


@Composable
fun WeekAndMonthTermRow(
    reportTermText: String,
    onClickedPrevButton: () -> Unit,
    onClickedNextButton: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(
            onClick = onClickedPrevButton
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "前",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Box(
            modifier = Modifier.weight(0.6F),
            contentAlignment = Alignment.Center) {
            Text(
                reportTermText,
                fontSize = 18.sp
            )
        }
        IconButton(
            onClick = onClickedNextButton
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "後",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DateTermRow(
    reportTermText: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
    ) {
        Box(
            modifier = modifier
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    reportTermText,
                    fontSize = 18.sp
                )
                Icon(imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "日付選択",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun DailyReportBox(
    taskList: List<Task>?,
    taskListSize: Int,
    boxHeight: Dp,
    taskClicked: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(boxHeight),
                contentAlignment = Alignment.Center
            ) {
                BoxNumberGrid(
                    modifier = Modifier.fillMaxSize(),
                    taskListSize = taskListSize
                )
                if(taskList != null) {
                    DailyTaskBoxContent(
                        taskList = taskList,
                        taskClicked = {taskClicked(it) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 40.dp),
                    )
                } else {
                    Text(
                        "データがありません",
                        modifier = Modifier
                            .background(Color.LightGray)
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyReportBox(
    viewModel: ReportViewModel,
    reportTasksState: Map<String,List<Task>>?,
    weekDatesList: List<LocalDate>,
    taskListSize: Int,
    weekDataTaskListHeight: Dp,
    taskClicked: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Column{
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(weekDataTaskListHeight + 24.dp)
            ) {
                BoxNumberGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 24.dp),
                    taskListSize = taskListSize
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 48.dp),
                    verticalAlignment = Alignment.Bottom,
                    content = {
                        itemsIndexed(weekDatesList) {index,date ->
                            Box(modifier = Modifier.fillParentMaxWidth(0.45F)) {
                                EachDateTaskContent(
                                    viewModel = viewModel,
                                    reportTaskState = reportTasksState!!,
                                    date = date.toString(),
                                    taskClicked = { taskClicked(it) },
                                    modifier = Modifier.fillMaxWidth(1f)
                                )
                            }
                            if(index < 6) {
                                Spacer(modifier = Modifier.fillParentMaxWidth(0.05F))
                            }
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun MonthlyReportBox(
    viewModel: ReportViewModel,
    reportTasksState: Map<String,List<Task>>?,
    monthDatesList: List<LocalDate>,
    taskListSize: Int,
    monthDataTaskListHeight: Dp,
    taskClicked: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Column{
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(monthDataTaskListHeight + 24.dp)
            ) {
                BoxNumberGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 24.dp),
                    taskListSize = taskListSize
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 48.dp),
                    verticalAlignment = Alignment.Bottom,
                    content = {
                        itemsIndexed(monthDatesList) {index,date ->
                            Box(modifier = Modifier.fillParentMaxWidth(0.45F)) {
                                EachDateTaskContent(
                                    viewModel = viewModel,
                                    reportTaskState = reportTasksState!!,
                                    date = date.toString(),
                                    taskClicked = { taskClicked(it) },
                                    modifier = Modifier.fillMaxWidth(1f)
                                )
                            }
                            if(index < 6) {
                                Spacer(modifier = Modifier.fillParentMaxWidth(0.05F))
                            }
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun DateSelectDialog(
    selectDateList: List<LocalDate>,
    dateClicked: (LocalDate) -> Unit,
    moreButtonClicked: () -> Unit,
    onDismissRequest:() -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {},
        text = {
            Text(
                "日付を選択",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 30.dp)
            ) {
                items(selectDateList) { date ->

                    val formattedDate = setTermTextByDate(date)

                    Text(
                        formattedDate,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable { dateClicked(date) }
                    )
                }
                item{
                    TextButton(
                        onClick = moreButtonClicked
                    ) {
                        Text("さらに前")
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxWidth(0.8F)
            .fillMaxHeight(0.5F)
            .verticalScroll(scrollState)
    )
}


@Composable
fun DailyTaskBoxContent(
    taskList: List<Task>,
    taskClicked: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
        ) {
            repeat(taskList.size) {iteration ->
                taskList.reversed().getOrNull(iteration)?.let { task ->
                    TaskBox(
                        task = task,
                        boxType = BoxTypeEnum.LARGE,
                        modifier = Modifier.fillMaxWidth(0.9F),
                        onClicked = { taskClicked(task) }
                    )
                }
            }
        }
    }
}


@Composable
fun EachDateTaskContent(
    viewModel: ReportViewModel,
    taskClicked: (Task) -> Unit,
    reportTaskState: Map<String,List<Task>>,
    date: String = "",
    modifier: Modifier = Modifier
) {

    val dateText = viewModel.formatDateString(date)


    Box(
        modifier = modifier
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            reportTaskState.entries.forEach {
                if(it.key == date) {
                    if(it.value.isNotEmpty()) {
                        repeat(it.value.size) { iteration ->
                            it.value.reversed().getOrNull(iteration)?.let { task ->
                                TaskBox(
                                    task = task,
                                    boxType = BoxTypeEnum.SMALL,
                                    modifier = Modifier.fillMaxWidth(0.9F),
                                    onClicked = { taskClicked(task) }
                                )
                            }
                        }
                    }
                }
            }
            Text(
                dateText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}



@Composable
fun DailyStaticContent(
    taskTotalWorkTime: String,
    taskTotalBoxNumber: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        DataRow(
            content = {
                DataBox(
                    "合計ツミアゲ時間",
                    taskTotalWorkTime,
                    Modifier.weight(0.45F)
                )
                DataBox(
                    "総ツミアゲ数",
                    "${taskTotalBoxNumber}ツミ",
                    Modifier.weight(0.45F)
                )
            }
        )
    }
}



@Composable
fun WeeklyAndMonthlyStaticContent(
    taskTotalWorkTime: String,
    taskAverageWorkTime: String,
    taskTotalBoxNumber: Int,
    taskAverageBoxNumber: Int,
    topBoxNumDate: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        DataRow(
            content = {
                DataBox(
                    "1日の平均時間",
                    taskAverageWorkTime,
                    Modifier.weight(0.45F)
                )
                DataBox(
                    "1日の平均ツミ",
                    "${taskAverageBoxNumber}ツミ",
                    Modifier.weight(0.45F)
                )
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        DataRow(
            content = {
                DataBox(
                    "総合計時間",
                    taskTotalWorkTime,
                    Modifier.weight(0.45F)
                )
                DataBox(
                    "総ツミアゲ数",
                    "${taskTotalBoxNumber}ツミ",
                    Modifier.weight(0.45F)
                )
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        DataRow(
            content = {
                DataBox(
                    "最多ツミアゲ日",
                    topBoxNumDate,
                    Modifier.weight(0.45F)
                )
            }
        )
    }
}

@Composable
fun DataRow(
    content: @Composable RowScope.()-> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        content = content
    )
}


@Composable
fun DataBox(
    dataTitle: String,
    data: String,
    modifier:Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
        ){
            Text(
                dataTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                data,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
