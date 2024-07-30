package com.example.tsumiageapp.ui.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.tsumiageapp.R
import com.example.tsumiageapp.data.model.CategoryTypeEnum
import com.example.tsumiageapp.data.model.Goal
import com.example.tsumiageapp.ui.common.functions.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalModalBottomSheet(
    goal: Goal,
    onDismissRequest: () -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var goalCategoryImage = R.drawable.resource_default
    var categoryBackGroundColor = Color.LightGray.copy(alpha = 0.2F)

    if(goal.categoryType != "") {
        val categoryTypeEnum = CategoryTypeEnum.valueOf(goal.categoryType)

        goalCategoryImage = getCategoryTypeImage(categoryTypeEnum)
        categoryBackGroundColor = getEachTaskBackGroundColor(categoryTypeEnum)
    }
    val time = changeMinuteToHourAndMinute(goal.time!!)
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 100.dp, topEnd = 100.dp),
        dragHandle = {},
        onDismissRequest = onDismissRequest
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(categoryBackGroundColor)
                .padding(top = 20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.fillMaxSize()
            ) {
                Text(
                    goal.categoryName,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Image(
                    painter = painterResource(goalCategoryImage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp)
                        .zIndex(1f),
                )
                Spacer(modifier = Modifier.height(28.dp))
                ModalContentColumn(
                    icon = Icons.Outlined.Create,
                    label = "タイトル",
                    text = goal.title
                )
                Spacer(modifier = Modifier.height(10.dp))
                ModalContentColumn(
                    icon = Icons.Outlined.Timer,
                    label = "時間",
                    text = time
                )
                Spacer(modifier = Modifier.height(10.dp))
                ModalContentColumn(
                    icon = Icons.Outlined.CalendarToday,
                    label = "追加日",
                    text = goal.addDate
                )
                Spacer(modifier = Modifier.height(20.dp))
                DeleteButton(
                    onClick = {
                        onDeleteClicked()
                            coroutineScope.launch {
                                sheetState.hide()
                            }
                    }
                )
            }
        }
    }
}