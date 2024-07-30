package com.example.tsumiageapp.ui.goal

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AssistantPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tsumiageapp.data.model.CategoryTypeEnum
import com.example.tsumiageapp.data.model.Goal
import com.example.tsumiageapp.data.model.GoalTypeEnum
import com.example.tsumiageapp.ui.common.functions.getCategoryBackGroundColor
import com.example.tsumiageapp.ui.common.functions.getCategoryTypeImage
import com.example.tsumiageapp.ui.common.components.ContentTitle
import com.example.tsumiageapp.ui.common.components.GoalBox
import com.example.tsumiageapp.ui.common.components.GoalModalBottomSheet
import com.example.tsumiageapp.ui.common.enums.BoxTypeEnum

enum class Screen(val title:String) {
    TODAY("今日"),TOMORROW("明日"),DAY("曜日別")
}

@Composable
fun GoalListScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: GoalViewModel = hiltViewModel()
    val todayGoalList by viewModel.todayGoalList.collectAsState()
    val tomorrowGoalList by viewModel.tomorrowGoalList.collectAsState()
    val goalListByDay by viewModel.goalListByDay.collectAsState()
    var todayGoalListHeight: Dp = 60.dp
    var tomorrowGoalListHeight: Dp = 60.dp

    //タブ
    var selectedTab: Screen by remember { mutableStateOf(Screen.TODAY) }

    if(todayGoalList.isNotEmpty()) {
        todayGoalListHeight = (todayGoalList.size * 120).dp
    }

    if(tomorrowGoalList.isNotEmpty()) {
        tomorrowGoalListHeight = (tomorrowGoalList.size * 120).dp
    }

    val scrollState = rememberScrollState()
    var selectedGoal: Goal? by remember { mutableStateOf(null) }
    var showGoalModalBottomSheet by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
        ) {
            TabRow(selectedTabIndex = selectedTab.ordinal) {
                Screen.entries.map {
                    Tab(
                        text = {
                            Text(
                                it.title,
                                color = if (selectedTab.ordinal == it.ordinal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
                        },
                        selected = selectedTab.ordinal == it.ordinal ,
                        onClick = {
                            selectedTab = it
                        },
                        modifier = Modifier.background(
                            color = MaterialTheme.colorScheme.background
                        )
                    )
                }
            }
            if(selectedTab == Screen.TODAY) {
                GoalListLayout(
                    goalList = todayGoalList,
                    modifier = Modifier
                        .height(todayGoalListHeight)
                        .padding(4.dp),
                    goalClicked = {
                        selectedGoal = it
                        showGoalModalBottomSheet = !showGoalModalBottomSheet
                    }
                )
            }
            if(selectedTab == Screen.TOMORROW) {
                GoalListLayout(
                    goalList = tomorrowGoalList,
                    modifier = Modifier
                        .height(tomorrowGoalListHeight)
                        .padding(4.dp),
                    goalClicked = {
                        selectedGoal = it
                        showGoalModalBottomSheet = !showGoalModalBottomSheet
                    }
                )
            }
            if(selectedTab == Screen.DAY) {
                //ContentTitle(
                //    icon = Icons.Outlined.AssistantPhoto,
                //    title = "曜日ごとの目標"
                //)
                GoalListByTabDay(
                    viewModel = viewModel
                )
                GoalListLayout(
                    goalList = goalListByDay,
                    modifier = Modifier.height(240.dp),
                    goalClicked = {
                        selectedGoal = it
                        showGoalModalBottomSheet = !showGoalModalBottomSheet
                    }
                )
            }
        }
        if(showGoalModalBottomSheet) {
            selectedGoal?.let {goal ->
                GoalModalBottomSheet(
                    goal = goal,
                    onDismissRequest = {
                        selectedGoal = null
                        showGoalModalBottomSheet = !showGoalModalBottomSheet
                    },
                    onDeleteClicked = {
                        viewModel.deleteGoal(goal)
                        selectedGoal = null
                        showGoalModalBottomSheet = !showGoalModalBottomSheet
                    }
                )
            }
        }
    }
}

@Composable
fun GoalListByTabDay(
    viewModel: GoalViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Box(modifier = modifier){
        TabRow(
            selectedTabIndex = selectedTab
        ) {
            GoalTypeEnum.entries.forEachIndexed { index, goal ->
                if(goal.title != "今日" && goal.title != "明日") {
                    Tab(
                        selected = selectedTab == (index -2),
                        onClick = {
                            selectedTab = (index -2)
                            viewModel.setGoalListByDay(goal)
                        },
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                color = MaterialTheme.colorScheme.background
                            )
                    ) {
                        Text(
                            text = if(goal.title != "毎日") goal.title[0].toString() else goal.title,
                            color = if(selectedTab == (index -2)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun GoalListLayout(
    goalList: List<Goal>,
    goalClicked:(goal:Goal) -> Unit,
    modifier:Modifier = Modifier
    ) {
    Box(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(4.dp)
        ) {
            if(goalList.isNotEmpty()) {
                items(goalList) { goal ->
                    GoalBox(
                        goal,
                        boxType = BoxTypeEnum.LARGE,
                        onClicked = { goalClicked(goal) },
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(0.9F)
                    )
                }
            } else {
                item{
                    Box(
                        modifier = Modifier.padding(top = 20.dp).fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("設定した目標がありません")
                    }
                }
            }
        }
    }
}

@Composable
fun GoalBox(
    goal: Goal,
    modifier: Modifier = Modifier
) {
    var goalCategoryImage: Int? = null
    var categoryBackGroundColor = Color(red = 0xFF,green = 0x5F,blue = 0x00, alpha = 0x20)

    if(goal.categoryType != "") {
        val categoryTypeEnum = CategoryTypeEnum.valueOf(goal.categoryType)

        goalCategoryImage = getCategoryTypeImage(categoryTypeEnum)

        categoryBackGroundColor = getCategoryBackGroundColor(categoryTypeEnum)
    }

    Box(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, categoryBackGroundColor, RoundedCornerShape(14.dp))
            .background(categoryBackGroundColor.copy(alpha = 0.4f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if(goalCategoryImage != null) {
                Image(
                    painter = painterResource(goalCategoryImage),
                    contentDescription = goal.categoryType,
                    modifier = Modifier.width(30.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Text(goal.title)
            Text(
                text = "${goal.time} 分",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
