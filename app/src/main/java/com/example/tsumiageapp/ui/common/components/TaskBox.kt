package com.example.tsumiageapp.ui.common.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.tsumiageapp.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.tsumiageapp.data.model.CategoryTypeEnum
import com.example.tsumiageapp.data.model.Task
import com.example.tsumiageapp.ui.common.enums.BoxTypeEnum
import com.example.tsumiageapp.ui.common.functions.changeMinuteToHourAndMinute
import com.example.tsumiageapp.ui.common.functions.getCategoryBackGroundColor
import com.example.tsumiageapp.ui.common.functions.getCategoryTypeImage


@Composable
fun TaskBox(
    task: Task,
    boxType: BoxTypeEnum,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var taskCategoryImage = R.drawable.resource_default
    var categoryBackGroundColor = Color.LightGray.copy(alpha = 0.5F)
    val time = changeMinuteToHourAndMinute(task.time!!)
    val boxHeight = boxType.boxHeight
    val roundedDp: Dp
    val boxBackgroundColor: Color

    //カテゴリーに応じた背景色・画像
    if(task.categoryType != "") {
        val categoryTypeEnum = CategoryTypeEnum.valueOf(task.categoryType)

        taskCategoryImage = getCategoryTypeImage(categoryTypeEnum)
        categoryBackGroundColor = getCategoryBackGroundColor(categoryTypeEnum)
    }

    when(boxType) {
        BoxTypeEnum.SMALL -> {
            roundedDp = 10.dp
            boxBackgroundColor = categoryBackGroundColor
        }
        BoxTypeEnum.LARGE -> {
            roundedDp = 20.dp
            boxBackgroundColor =  Color.White
        }
    }

    Box(
        modifier = modifier
            .size(boxHeight.dp)
            .clip(RoundedCornerShape(roundedDp))
            .border(1.dp, Color.DarkGray.copy(alpha = 0.3f), RoundedCornerShape(roundedDp))
            .background(boxBackgroundColor)
            .clickable { onClicked() }
    ) {
        if(boxType == BoxTypeEnum.LARGE) {
            TaskBoxCircle(
                backgroundColor = categoryBackGroundColor,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                TaskCategory(
                    categoryName = task.categoryName,
                    categoryImage = taskCategoryImage,
                    modifier = Modifier.weight(0.25F)
                )
                Spacer(modifier = Modifier.weight(0.1f))
                TaskContent(
                    taskTitle = task.title,
                    taskTime = time,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .weight(0.65f)
                )
            }
        } else {
            SmallTaskContent(
                task = task,
                categoryImage = taskCategoryImage,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun TaskBoxCircle(
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .height(90.dp)
    ) {
        val circleRadius = size.height * 2
        drawCircle(
            color = backgroundColor,
            radius = circleRadius,
            center = Offset(x = size.width / 7 * 6, y = size.height / 2)
        )
    }
}

@Composable
fun TaskCategory(
    categoryName: String,
    categoryImage: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .zIndex(1f)
            .padding(start = 8.dp)
    ){
        Image(
            painter = painterResource(categoryImage),
            contentDescription = null,
            modifier = Modifier.size(50.dp),
        )
        Text(
            categoryName,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun TaskContent(
    taskTitle: String,
    taskTime: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(start = 8.dp)
    ) {
        Text(
            text = taskTitle,
            fontWeight = FontWeight.Bold
        )
        Text(text = taskTime)
    }
}

@Composable
fun SmallTaskContent(
    task: Task,
    categoryImage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(categoryImage),
            contentDescription = task.categoryType,
            modifier = Modifier
                .weight(0.3f)
                .size(40.dp)
                .zIndex(1f)
                .padding(start = 4.dp),
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .weight(0.7f)
                .background(Color.White)
                .padding(start = 12.dp)
        ) {
            Text(
                text = task.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}