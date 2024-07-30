package com.example.tsumiageapp.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.tsumiageapp.R
import com.example.tsumiageapp.data.model.Task
import com.example.tsumiageapp.ui.common.components.*
import com.example.tsumiageapp.ui.common.enums.BoxTypeEnum
import com.example.tsumiageapp.ui.common.functions.*
import java.time.LocalDate
import kotlinx.coroutines.delay


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    val viewModel:HomeViewModel = hiltViewModel()
    val todayTasks by viewModel.todayTasksUiState.collectAsState()
    val theseDaysTasks by viewModel.theseDaysTasksState.collectAsState()
    val todayGoalList by viewModel.todayGoalListState.collectAsState()

    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    val dayBeforeYesterday = today.minusDays(2)

    val yesterdayTasks = theseDaysTasks.filter {it.date == yesterday.toString()}
    val dayBeforeYesterdayTasks = theseDaysTasks.filter { it.date == dayBeforeYesterday.toString() }

    val pastTaskBoxNumber = maxOf(yesterdayTasks.size, dayBeforeYesterdayTasks.size)

    val todayTaskListHeight :Dp = getTaskListContentHeightByBoxNumber(todayTasks.size, BoxTypeEnum.LARGE.boxHeight).dp
    val pastTaskListHeight :Dp = getTaskListContentHeightByBoxNumber(pastTaskBoxNumber, BoxTypeEnum.SMALL.boxHeight).dp

    val todayTaskScrollState = rememberScrollState()
    val pastFewDaysTaskScrollState = rememberScrollState()


    var showModalBottomSheet by remember { mutableStateOf(false) }
    var selectedTask: Task? by remember { mutableStateOf(null) }


    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        if(todayGoalList.isNotEmpty()) {
            item{
                CardContent(
                    content = {
                        ProgressIndicator(viewModel = viewModel)
                    }
                )
            }
        }
        item{
            ContentTitle(
                icon = Icons.Outlined.Info,
                title = "今日のツミアゲ"
            )
        }
        item{
            CardContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                content = {
                    TodayTasksBox(
                        todayTasks = todayTasks,
                        boxHeight = todayTaskListHeight,
                        taskClicked = {
                            selectedTask = it
                            showModalBottomSheet = !showModalBottomSheet
                        },
                        modifier = Modifier
                            .height(560.dp)
                            .background(Color.White)
                            .padding(bottom = 18.dp)
                            .verticalScroll(todayTaskScrollState),
                    )
                    if(todayTasks.isNotEmpty()) {
                        LaunchedEffect(todayTaskListHeight) {
                            todayTaskScrollState.animateScrollTo(todayTaskScrollState.maxValue)
                        }
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
                }
            )
        }
        item {
            ContentTitle(
                icon = Icons.Outlined.Analytics,
                title = "最近のツミアゲ"
            )
        }
        item {
            CardContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                content = {
                    PastTasksListBox(
                        yesterdayTasks = yesterdayTasks,
                        dayBeforeYesterdayTasks = dayBeforeYesterdayTasks,
                        pastTaskBoxNumber = pastTaskBoxNumber,
                        boxHeight = pastTaskListHeight,
                        taskClicked = {
                            selectedTask = it
                            showModalBottomSheet = !showModalBottomSheet
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .height(420.dp)
                            .verticalScroll(pastFewDaysTaskScrollState)
                    )
                    if(dayBeforeYesterdayTasks.isNotEmpty() || yesterdayTasks.isNotEmpty()) {
                        LaunchedEffect(pastTaskListHeight) {
                            pastFewDaysTaskScrollState.animateScrollTo(pastFewDaysTaskScrollState.maxValue)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun TodayTasksBox(
    todayTasks: List<Task>,
    boxHeight: Dp,
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
                    .height(boxHeight + 20.dp),
                contentAlignment = Alignment.Center
            ) {
                BoxNumberGrid(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxSize(),
                    taskListSize = todayTasks.size
                )
                if(todayTasks.isNotEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    ) {
                        repeat(todayTasks.size) {iteration ->
                            todayTasks.reversed().getOrNull(iteration)?.let { task ->

                                var visible by remember{ mutableStateOf(false) }

                                LaunchedEffect(Unit) {
                                    delay((todayTasks.size - iteration) * 200L)
                                    visible = true
                                }

                                AnimatedTaskBox(
                                    task = task,
                                    visible = visible,
                                    taskClicked = { taskClicked(it) },
                                    modifier = Modifier.fillMaxWidth(0.9F)
                                )
                            }
                        }
                    }
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
fun AnimatedTaskBox(
    task: Task,
    taskClicked: (Task) -> Unit,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it * 5 },
            animationSpec = tween(durationMillis = 800)
        ) + fadeIn(
            initialAlpha = 0.2F,
            animationSpec = tween(durationMillis = 800)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 800)
        ) + fadeOut(
            animationSpec = tween(durationMillis = 800)
        )
    ) {
        TaskBox(
            task = task,
            boxType = BoxTypeEnum.LARGE,
            onClicked = { taskClicked(task) },
            modifier = modifier.fillMaxWidth(0.9F)
        )
    }
}


@Composable
fun PastTasksListBox(
    yesterdayTasks: List<Task>,
    dayBeforeYesterdayTasks: List<Task>,
    pastTaskBoxNumber: Int,
    boxHeight: Dp,
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
                    .height(boxHeight + 38.dp)
            ) {
                BoxNumberGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 38.dp),
                    taskListSize = pastTaskBoxNumber
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 50.dp, end = 8.dp, top = 12.dp, bottom = 12.dp)
                ) {
                    PastTaskListColumn(
                        taskList = dayBeforeYesterdayTasks,
                        taskClicked = {
                            taskClicked(it)
                        },
                        label = "一昨日のツミアゲ",
                        modifier = Modifier.weight(1F)
                    )
                    Spacer(modifier = Modifier.weight(0.2F))
                    PastTaskListColumn(
                        taskList = yesterdayTasks,
                        taskClicked = {
                            taskClicked(it)
                        },
                        label = "昨日のツミアゲ",
                        modifier = Modifier.weight(1F)
                    )
                }
            }
        }
    }
}

@Composable
fun PastTaskListColumn(
    taskList: List<Task>,
    taskClicked: (Task) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(taskList.size) { iteration ->
            taskList.reversed().getOrNull(iteration)?.let { task ->
                TaskBox(
                    task =  task,
                    boxType = BoxTypeEnum.SMALL,
                    onClicked = { taskClicked(task) },
                    modifier = Modifier.fillMaxWidth(1F)
                )
            }
        }
        Text(
            label,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}


@Composable
fun ProgressIndicator(viewModel: HomeViewModel) {
    val todayGoalList by viewModel.todayGoalListState.collectAsState()
    val todayIncompleteGoalList by viewModel.todayIncompleteGoalList.collectAsState()
    val todayCompleteGoalList by viewModel.todayCompleteGoalList.collectAsState()

    var currentTaskIndex by remember { mutableIntStateOf(0) }
    val totalGoals = todayGoalList.size
    val totalCompleteGoals = todayCompleteGoalList.size
    val totalIncompleteGoals = todayIncompleteGoalList.size
    val colorScheme = MaterialTheme.colorScheme
    // 円形のプログレスバーのアニメーション
    val animatedProgress = remember { androidx.compose.animation.core.Animatable(0f) }


    LaunchedEffect(Unit) {
        viewModel.getIncompleteGoals()
    }
    if(totalCompleteGoals != totalGoals) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
        ) {
            Text(
                "今日の目標達成まであと${totalIncompleteGoals}ツミ！",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 未達成のタスク表示
                if (todayIncompleteGoalList.isNotEmpty()) {
                    GoalBox(
                        goal = todayIncompleteGoalList[currentTaskIndex],
                        boxType = BoxTypeEnum.SMALL,
                        onClicked = { },
                        modifier = Modifier.fillMaxWidth(0.75f)
                    )
                }
                // 円形のプログレスバーと進行状況表示
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(60.dp)
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val sweepAngle = 360 * animatedProgress.value
                        drawArc(
                            color = Color.LightGray,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 15f,
                                cap = StrokeCap.Round
                            )
                        )
                        drawArc(
                            color = colorScheme.primary,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 15f,
                                cap = StrokeCap.Round
                            )
                        )
                    }
                    Text(
                        text = "${totalCompleteGoals}/${totalGoals}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }

    // タスクの切り替え
    if(todayIncompleteGoalList.isNotEmpty()) {
        LaunchedEffect(totalIncompleteGoals) {
            while (true) {
                delay(3000)
                currentTaskIndex = (currentTaskIndex + 1) % totalIncompleteGoals
            }
        }
    }

    if(todayCompleteGoalList.isNotEmpty()) {
        LaunchedEffect(totalCompleteGoals) {
            animatedProgress.animateTo(
                targetValue = totalCompleteGoals / totalGoals.toFloat(),
                animationSpec = tween(durationMillis = 1000)
            )
        }
    }
}