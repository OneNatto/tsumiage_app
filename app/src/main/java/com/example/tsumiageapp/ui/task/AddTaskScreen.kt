package com.example.tsumiageapp.ui.task

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.tsumiageapp.R
import com.example.tsumiageapp.data.model.Category
import com.example.tsumiageapp.data.model.CategoryTypeEnum
import com.example.tsumiageapp.data.model.Goal
import com.example.tsumiageapp.ui.common.components.*
import com.example.tsumiageapp.ui.common.functions.getCategoryTypeImage
import com.example.tsumiageapp.ui.common.enums.BoxTypeEnum
import kotlinx.coroutines.delay
import java.time.LocalDate

enum class AddTaskScreen(val title: String) {
    NEW("新しくツミアゲ"), GOAL("目標からツミアゲ")
}

@Composable
fun AddTaskScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: TaskViewModel = hiltViewModel()
    val taskNameState by viewModel.taskNameUiState.collectAsState()
    val todayGoalMap by viewModel.todayGoalMap.collectAsState()
    val selectedHourState by viewModel.selectedHourState.collectAsState()
    val selectedMinuteState by viewModel.selectedMinuteState.collectAsState()
    val categoryList by viewModel.categoryListUiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedCategory by viewModel.selectedCategoryState.collectAsState()
    val isFormValidate by viewModel.isFormValidate.collectAsState()

    var showDatePickDialog by remember { mutableStateOf(false) }
    var showCategoryAddDialog by remember { mutableStateOf(false) }
    var showTaskCompletedDialog by remember { mutableStateOf(false) }
    var showWorkTimePickerDialog by remember { mutableStateOf(false) }

    var currentScreen by remember { mutableStateOf(AddTaskScreen.NEW) }


    if (showDatePickDialog) {
        LaunchedEffect(Unit) {
            viewModel.showDatePickerDialog(context)
            showDatePickDialog = false
        }
    }

    fun backToHome() {
        showTaskCompletedDialog = false
        navController.popBackStack()
    }

    Column(
        modifier = modifier
    ) {

        //タスク追加タイプ選択
        AddTaskTypeTab(
            onTabClicked = { currentScreen = it },
            modifier = Modifier.fillMaxWidth()
        )

        if(currentScreen == AddTaskScreen.NEW) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                //タイトル
                TitleField(
                    value = taskNameState,
                    label = "タイトル",
                    onValueChange = { viewModel.setTaskTitle(it) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                //時間
                TimeField(
                    hour = selectedHourState,
                    minute = selectedMinuteState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showWorkTimePickerDialog = !showWorkTimePickerDialog
                        }
                )
                if (showWorkTimePickerDialog) {
                    WorkTimePickerDialog(
                        taskViewModel = viewModel,
                        onConfirm = { showWorkTimePickerDialog = !showWorkTimePickerDialog },
                        onDismissRequest = { showWorkTimePickerDialog = !showWorkTimePickerDialog }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                //カテゴリー選択
                CategoryField(
                    categoryList = categoryList,
                    selectedCategory = selectedCategory,
                    onCategoryClicked = { viewModel.selectCategory(it) },
                    addNewCategoryFunction = { showCategoryAddDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.DarkGray, ShapeDefaults.ExtraSmall)
                        .background(Color.White)
                )
                if (showCategoryAddDialog) {
                    CategoryAddDialog(
                        viewModel = viewModel,
                        onDismissRequest = {
                            showCategoryAddDialog = false
                        },
                        onSave = {
                            showCategoryAddDialog = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                TaskDateField(
                    selectedDate = selectedDate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePickDialog = true }
                )

                Spacer(modifier = Modifier.height(4.dp))
                AddButton(
                    text = "ツミアゲる",
                    onClicked = {
                        viewModel.addTask()
                        showTaskCompletedDialog = true
                    },
                    isFormValidate = isFormValidate,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            //目標から追加
            LaunchedEffect(Unit) {
                viewModel.getComparedTaskAndGoal()
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 12.dp)
            ) {
                repeat(todayGoalMap.size) {iteration ->
                    todayGoalMap.getOrNull(iteration)?.let{ goalMap ->
                        goalMap.entries.forEach { goal ->
                            TodayGoalCard(
                                goal = goal,
                                onButtonClicked = { viewModel.addTaskFromGoal(goal.value)
                                    showTaskCompletedDialog = !showTaskCompletedDialog },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }
        }
    }
    if(showTaskCompletedDialog) {
        LaunchedEffect(Unit) {
            delay(4000)
            backToHome()
        }
        TaskCompletedDialog(
            onDismissRequest = { backToHome() }
        )
    }
}


@Composable
fun AddTaskTypeTab(
    onTabClicked:(AddTaskScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    TabRow(
        selectedTabIndex = selectedTab,
        modifier = modifier
    ) {
        AddTaskScreen.entries.forEachIndexed { index, addTaskScreen ->
            Tab(
                selected = selectedTab == addTaskScreen.ordinal,
                onClick = {
                    selectedTab = addTaskScreen.ordinal
                    onTabClicked(addTaskScreen)
                },
                modifier = Modifier
                    .height(50.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Text(
                    text = addTaskScreen.title,
                    fontSize = 15.sp,
                    color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun TaskDateField(
    selectedDate: LocalDate?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedDate?.toString() ?: "日付を選択",
            onValueChange = {},
            enabled = false,
            readOnly = true,
            label = { Text("ツミアゲ日") },
            colors = TextFieldDefaults.colors(
                disabledContainerColor = Color.White,
                disabledTextColor = if (selectedDate != null) Color.Black else Color.Red,
                disabledLabelColor = if (selectedDate != null) Color.Black else Color.Red,
                disabledIndicatorColor = if (selectedDate != null) Color.Black else Color.Red
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TodayGoalCard(
    goal: Map.Entry<Boolean, Goal>,
    onButtonClicked:() -> Unit,
    modifier: Modifier = Modifier
) {
    CardContent(
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                GoalBox(
                    goal = goal.value,
                    boxType = BoxTypeEnum.LARGE,
                    onClicked = {},
                    modifier = Modifier.fillMaxWidth(0.9F)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onButtonClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    enabled = !goal.key,
                    modifier = Modifier
                        .fillMaxWidth(0.8F)
                ) {
                    Text(
                        text = if(!goal.key) "この目標をツミアゲ" else "達成済みです！",
                        color = if(!goal.key) Color.White else Color.DarkGray
                    )
                }
            }
        },
        modifier = modifier
    )
}


@Composable
fun CategoryAddDialog(
    viewModel: TaskViewModel,
    onDismissRequest: () -> Unit,
    onSave: () -> Unit
) {
    val addCategoryValue by viewModel.categoryNameUiState.collectAsState()
    val newCategoryType by viewModel.newCategoryTypeState.collectAsState()
    var categoryTypeMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        text = {
               Column {
                   Column(
                       modifier = Modifier
                           .fillMaxWidth()
                   ) {
                       NewCategoryTypeField(
                           newCategoryType = newCategoryType,
                           modifier = Modifier
                               .fillMaxWidth()
                               .border(1.dp, Color.DarkGray, ShapeDefaults.ExtraSmall)
                               .clickable {
                                   categoryTypeMenuExpanded = !categoryTypeMenuExpanded
                               }
                       )
                       CategoryTypeDropDown(
                           categoryTypeMenuExpanded = categoryTypeMenuExpanded,
                           onDismissRequest = { categoryTypeMenuExpanded = !categoryTypeMenuExpanded },
                           onTypeClicked = { viewModel.setNewCategoryType(it) },
                       )
                   }
                   Spacer(modifier = Modifier.height(12.dp))
                   TextField(
                       value = addCategoryValue,
                       onValueChange = { viewModel.setCategoryName(it) }
                   )
               }
        },
        onDismissRequest = {
            viewModel.clearCategoryName()
            onDismissRequest()
                           },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("キャンセル")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                viewModel.addCategory()
                onSave()
            }) {
                Text("追加")
            }
        }
    )
}

@Composable
fun CategoryTypeRow(
    category: Category? = null,
    categoryTypeEnum: CategoryTypeEnum? = null,
    categoryTypeImage: Int?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        if(categoryTypeImage != null) {
            Image(
                painter = painterResource(categoryTypeImage),
                contentDescription = "",
                modifier = Modifier.height(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        if(category != null) {
            Text(category.name)
        }
        if(categoryTypeEnum != null) {
            Text(categoryTypeEnum.title)
        }
    }
}

@Composable
fun TaskCompletedDialog(
    onDismissRequest: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.congratulation_party))
    AlertDialog(
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4F),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    "ツミアゲおつかれさま！",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {}
    )
}