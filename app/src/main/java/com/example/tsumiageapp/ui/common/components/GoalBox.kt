package com.example.tsumiageapp.ui.common.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.tsumiageapp.R
import com.example.tsumiageapp.data.model.CategoryTypeEnum
import com.example.tsumiageapp.data.model.Goal
import com.example.tsumiageapp.ui.common.enums.BoxTypeEnum
import com.example.tsumiageapp.ui.common.functions.changeMinuteToHourAndMinute
import com.example.tsumiageapp.ui.common.functions.getCategoryBackGroundColor
import com.example.tsumiageapp.ui.common.functions.getCategoryTypeImage

@Composable
fun GoalBox(
    goal: Goal,
    boxType: BoxTypeEnum,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var goalCategoryImage = R.drawable.resource_default
    var categoryBackGroundColor = Color.LightGray.copy(alpha = 0.5F)
    val time = changeMinuteToHourAndMinute(goal.time)
    val boxHeight = boxType.boxHeight

    //カテゴリーに応じた背景色・画像
    if(goal.categoryType != "") {
        val categoryTypeEnum = CategoryTypeEnum.valueOf(goal.categoryType)

        goalCategoryImage = getCategoryTypeImage(categoryTypeEnum)
        categoryBackGroundColor = getCategoryBackGroundColor(categoryTypeEnum)
    }

    Box(
        modifier = modifier
            .size(boxHeight.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color.DarkGray.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .background(categoryBackGroundColor.copy(alpha = 0.4f))
            .clickable { onClicked() }
    ) {
        GoalBoxCircle(
            boxType = boxType,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            GoalCategory(
                categoryName = goal.categoryName,
                goalCategoryImage = goalCategoryImage,
                modifier = Modifier.weight(0.25F)
            )
            Spacer(modifier = Modifier.weight( if(boxType == BoxTypeEnum.LARGE) 0.1f else 0.05f))
            GoalContent(
                goalTitle = goal.title,
                goalTime = time,
                modifier = Modifier.fillMaxSize()
                    .weight(if(boxHeight == 90) 0.65f else 0.6f)
            )
        }
    }
}


@Composable
fun GoalBoxCircle(
    boxType: BoxTypeEnum,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .height(180.dp)
    ) {
        val circleRadius = size.height * 1
        drawCircle(
            color = Color.White,
            radius = circleRadius,
            center = if(boxType == BoxTypeEnum.LARGE) Offset(x = size.width / 7 * 4, y = size.height / 2) else Offset(x = size.width /  2, y = size.height / 2)
        )
    }
}

@Composable
fun GoalCategory(
    categoryName: String,
    goalCategoryImage: Int,
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
            painter = painterResource(goalCategoryImage),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
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
fun GoalContent(
    goalTitle: String,
    goalTime: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .background(Color.White)
            .padding(start = 8.dp)
    ) {
        Text(
            text = goalTitle,
            fontWeight = FontWeight.Bold
        )
        Text(goalTime)
    }
}