package com.example.tsumiageapp.ui.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tsumiageapp.ui.goal.GoalViewModel
import com.example.tsumiageapp.ui.task.TaskViewModel
import kotlin.math.abs

@Composable
fun WorkTimePickerDialog(
    goalViewModel: GoalViewModel? = null,
    taskViewModel: TaskViewModel? = null,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val minuteList = listOf(null,0,10,20,30,40,50,null)
    val hourLazyListState = rememberLazyListState()
    val minuteLazyListState = rememberLazyListState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("決定")
            } },
        text = {
            Row(
                modifier = Modifier
                    .height(180.dp)
                    .padding(vertical = 28.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(goalViewModel != null) {
                    HourLazyList(
                        goalViewModel = goalViewModel,
                        lazyListState = hourLazyListState,
                        modifier = Modifier.weight(1f)
                    )
                } else if(taskViewModel != null) {
                    HourLazyList(
                        taskViewModel = taskViewModel,
                        lazyListState = hourLazyListState,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text("時間")
                Spacer(modifier = Modifier.width(16.dp))

                if(goalViewModel != null) {
                    MinuteLazyList(
                        minuteList = minuteList,
                        goalViewModel = goalViewModel,
                        lazyListState = minuteLazyListState,
                        modifier = Modifier.weight(1f)
                    )
                } else if(taskViewModel != null) {
                    MinuteLazyList(
                        minuteList = minuteList,
                        taskViewModel = taskViewModel,
                        lazyListState = minuteLazyListState,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text("分")

                LaunchedEffect(hourLazyListState) {
                    snapshotFlow { hourLazyListState.layoutInfo.visibleItemsInfo }
                        .collect { visibleItems ->
                            val viewportCenter = hourLazyListState.layoutInfo.viewportEndOffset / 2
                            val selectedHourIndex = visibleItems.minByOrNull {
                                abs((it.offset + it.size / 2) - viewportCenter)
                            }?.index
                            selectedHourIndex?.let {
                                goalViewModel?.setGoalHour(it -1)
                                taskViewModel?.selectHour(it -1)
                            }
                        }
                }

                LaunchedEffect(minuteLazyListState) {
                    snapshotFlow { minuteLazyListState.layoutInfo.visibleItemsInfo }
                        .collect { visibleItems ->
                            val viewportCenter = minuteLazyListState.layoutInfo.viewportEndOffset / 2
                            val selectedMinuteIndex = visibleItems.minByOrNull {
                                abs((it.offset + it.size / 2) - viewportCenter)
                            }?.index

                            selectedMinuteIndex?.let {index ->
                                val selectedMinute = minuteList.getOrNull(index)
                                if(selectedMinute != null) {
                                    goalViewModel?.setGoalMinute(selectedMinute)
                                    taskViewModel?.selectMinute(selectedMinute)
                                }
                            }
                        }
                }
            }
        },
    )
}

@Composable
fun HourLazyList(
    goalViewModel: GoalViewModel? = null,
    taskViewModel: TaskViewModel? = null,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    val hourList = listOf(null) + (0..6).toList() + listOf(null)

    // 時間
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(hourList) { index, hour ->
            if(hour != null) {
                val isSelected = remember{
                    derivedStateOf {
                        val viewportCenter = (lazyListState.layoutInfo.viewportEndOffset - lazyListState.layoutInfo.viewportStartOffset) / 2
                        val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
                        val selectedHourIndex = visibleItems.minByOrNull {
                            abs(it.offset + it.size / 2 - viewportCenter)
                        }?.index

                        selectedHourIndex != null && selectedHourIndex == index
                    }
                }

                Text(
                    text = hour.toString(),
                    fontWeight = if (isSelected.value) FontWeight.Bold else FontWeight.Light,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            goalViewModel?.setGoalHour(hour)
                            taskViewModel?.selectHour(hour)
                        }
                )
            } else {
                Spacer(modifier = Modifier.height(44.dp)) // 適当な高さのスペーサーを入れてダミー要素に見えないようにします
            }
        }

    }
}

@Composable
fun MinuteLazyList(
    minuteList: List<Int?>,
    goalViewModel: GoalViewModel? = null,
    taskViewModel: TaskViewModel? = null,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    // 分数
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(minuteList) { minute ->
            val isSelected = remember {
                derivedStateOf {
                    val viewportCenter = (lazyListState.layoutInfo.viewportEndOffset - lazyListState.layoutInfo.viewportStartOffset) / 2
                    val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
                    val selectedMinuteIndex = visibleItems.minByOrNull {
                        abs((it.offset + it.size / 2) - viewportCenter)
                    }?.index
                    selectedMinuteIndex != null && minuteList.getOrNull(selectedMinuteIndex) == minute
                }
            }
            if(minute != null) {
                Text(
                    text = minute.toString(),
                    fontWeight = if (isSelected.value) FontWeight.Bold else FontWeight.Light,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            goalViewModel?.setGoalMinute(minute)
                            taskViewModel?.selectMinute(minute)
                        }
                )
            } else {
                Spacer(modifier = Modifier.height(44.dp)) // 適当な高さのスペーサーを入れてダミー要素に見えないようにします
            }
        }
    }
}