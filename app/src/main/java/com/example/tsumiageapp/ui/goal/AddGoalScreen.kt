package com.example.tsumiageapp.ui.goal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tsumiageapp.data.model.Category
import com.example.tsumiageapp.data.model.CategoryTypeEnum
import com.example.tsumiageapp.data.model.GoalTypeEnum
import com.example.tsumiageapp.ui.common.components.*
import com.example.tsumiageapp.ui.common.functions.getCategoryTypeImage
import kotlin.math.abs

@Composable
fun AddGoalScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: GoalViewModel = hiltViewModel()
    val goalNameState by viewModel.goalNameState.collectAsState()
    val selectedHourState by viewModel.selectedGoalHourState.collectAsState()
    val selectedMinuteState by viewModel.selectedGoalMinuteState.collectAsState()
    val selectedGoalType by viewModel.selectedGoalType.collectAsState()
    val categoryList by viewModel.categoryListState.collectAsState()
    val selectedCategory by viewModel.selectedGoalCategory.collectAsState()
    val isFormValidate by viewModel.isFormValidate.collectAsState()

    var showCategoryAddDialog by remember { mutableStateOf(false) }
    var showWorkTimePickerDialog by remember { mutableStateOf(false) }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        //タイトル入力
        TitleField(
            value = goalNameState,
            label = "タイトル",
            onValueChange = { viewModel.setGoalTitle(it) },
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
        if(showWorkTimePickerDialog) {
            WorkTimePickerDialog(
                goalViewModel = viewModel,
                onConfirm = { showWorkTimePickerDialog = !showWorkTimePickerDialog },
                onDismissRequest = { showWorkTimePickerDialog = !showWorkTimePickerDialog }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        //カテゴリー選択
        CategoryField(
            categoryList = categoryList,
            selectedCategory = selectedCategory,
            onCategoryClicked = { viewModel.setGoalCategory(it) },
            addNewCategoryFunction = { showCategoryAddDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.DarkGray, ShapeDefaults.ExtraSmall)
                .background(Color.White)
        )
        if(showCategoryAddDialog) {
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
        //目標設定の選択
        GoalTypeField(
            selectedGoalType = selectedGoalType,
            onMenuItemClicked = { viewModel.setGoalType(it) },
            modifier = Modifier.fillMaxWidth()
                .border(1.dp, Color.DarkGray, ShapeDefaults.ExtraSmall)
                .background(Color.White)
        )

        Spacer(modifier = Modifier.height(4.dp))
        AddButton(
            text = "目標を追加",
            onClicked = {
                viewModel.addGoal()
                navController.popBackStack()
            },
            isFormValidate = isFormValidate,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun GoalTypeField(
    selectedGoalType: GoalTypeEnum,
    onMenuItemClicked: (GoalTypeEnum) -> Unit,
    modifier: Modifier = Modifier
) {
    var goalTypeMenuExpanded by remember { mutableStateOf(false) }

    Column{
        Box(
            modifier = modifier
                .clickable { goalTypeMenuExpanded = !goalTypeMenuExpanded }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = selectedGoalType.title
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "選択"
                )
            }
        }
        //ドロップダウンメニュー
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            DropdownMenu(
                expanded = goalTypeMenuExpanded,
                onDismissRequest = { goalTypeMenuExpanded = false }
            ) {
                GoalTypeEnum.entries.forEach { type ->
                    DropdownMenuItem(
                        text = {
                            Text(type.title)
                        },
                        onClick = {
                            onMenuItemClicked(type)
                            goalTypeMenuExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryAddDialog(
    viewModel: GoalViewModel,
    onDismissRequest: () -> Unit,
    onSave: () -> Unit
) {
    val newCategoryName by viewModel.newCategoryNameState.collectAsState()
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
                        modifier = Modifier.fillMaxWidth()
                            .border(1.dp, Color.DarkGray, ShapeDefaults.ExtraSmall)
                            .clickable {
                                categoryTypeMenuExpanded = !categoryTypeMenuExpanded
                            }
                    )
                    CategoryTypeDropDown(
                        categoryTypeMenuExpanded = categoryTypeMenuExpanded,
                        onDismissRequest = { categoryTypeMenuExpanded = !categoryTypeMenuExpanded },
                        onTypeClicked = { viewModel.setNewCategoryName(it) },
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = newCategoryName,
                    onValueChange = { viewModel.setNewCategoryName(it) }
                )
            }
        },
        onDismissRequest = {
            viewModel.clearNewCategory()
            onDismissRequest()
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("キャンセル")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                viewModel.addNewCategory()
                onSave()
            }) {
                Text("追加")
            }
        }
    )
}