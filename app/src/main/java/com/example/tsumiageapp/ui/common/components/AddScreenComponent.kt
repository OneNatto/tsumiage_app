package com.example.tsumiageapp.ui.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.tsumiageapp.data.model.Category
import com.example.tsumiageapp.data.model.CategoryTypeEnum
import com.example.tsumiageapp.ui.common.functions.getCategoryTypeImage
import com.example.tsumiageapp.ui.goal.GoalViewModel
import com.example.tsumiageapp.ui.task.CategoryTypeRow
import com.example.tsumiageapp.ui.task.TaskViewModel


//タイトル入力
@Composable
fun TitleField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = "タイトル"
            )
        },
        label = {
            Text(label)
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        colors = TextFieldDefaults.colors(
            focusedLabelColor = Color.Black,
            focusedIndicatorColor = Color.Black,
            focusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = modifier
    )
}

//時間
@Composable
fun TimeField(
    hour: Int?,
    minute: Int?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = "${hour ?: 0} 時間 ${minute ?: 0} 分",
            onValueChange = {},
            label = { Text("作業時間") },
            enabled = false,
            readOnly = true,
            colors = TextFieldDefaults.colors(
                disabledContainerColor = Color.White,
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Black,
                disabledIndicatorColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

//カテゴリー選択
@Composable
fun CategoryField(
    categoryList: List<Category>,
    selectedCategory: Category,
    onCategoryClicked:(Category) -> Unit,
    addNewCategoryFunction: () -> Unit,
    modifier: Modifier =Modifier
) {
    var selectedCategoryTypeImage: Int?
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    Column {
        Box(
            modifier = modifier
                .clickable { categoryMenuExpanded = !categoryMenuExpanded }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (selectedCategory.categoryType != "") {
                    val selectedCategoryTypeEnum = CategoryTypeEnum.valueOf(selectedCategory.categoryType)

                    selectedCategoryTypeImage = getCategoryTypeImage(selectedCategoryTypeEnum)

                    Image(
                        painter = painterResource(id = selectedCategoryTypeImage!!),
                        contentDescription = selectedCategory.categoryType,
                        modifier = Modifier.height(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Text(
                    text = if (selectedCategory.name != "") selectedCategory.name else "カテゴリーを選択"
                )
                if (selectedCategory.name == "") {
                    Spacer(modifier = Modifier.weight(1F))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "選択"
                    )
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            DropdownMenu(
                expanded = categoryMenuExpanded,
                onDismissRequest = { categoryMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("なし")
                    },
                    onClick = {
                        categoryMenuExpanded = false
                    }
                )
                categoryList.forEach { category ->

                    //カテゴリー対応画像の取得
                    var categoryTypeImage: Int? = null
                    if (category.categoryType != "") {
                        val categoryTypeEnum = CategoryTypeEnum.valueOf(category.categoryType)

                        categoryTypeImage = getCategoryTypeImage(categoryTypeEnum)
                    }

                    DropdownMenuItem(
                        text = {
                            CategoryTypeRow(
                                category = category,
                                categoryTypeImage = categoryTypeImage
                            )
                        },
                        onClick = {
                            onCategoryClicked(category)
                            categoryMenuExpanded = false
                        }
                    )
                }
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "追加"
                            )
                            Text("カテゴリーを追加する")
                        }
                    },
                    onClick = { addNewCategoryFunction()}
                )
            }
        }
    }
}

@Composable
fun AddButton(
    text: String,
    onClicked: () -> Unit,
    isFormValidate: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClicked,
        modifier = modifier,
        enabled = isFormValidate
    ) {
        Text(
            text,
            color = if (isFormValidate) Color.White else Color.Gray
        )
    }
}


@Composable
fun NewCategoryTypeField(
    newCategoryType: CategoryTypeEnum?,
    modifier: Modifier = Modifier
) {
    var newCategoryTypeImage: Int? = null

    Box(
        modifier = modifier
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            if(newCategoryType != null) {
                newCategoryTypeImage = getCategoryTypeImage(newCategoryType)
            }
            if(newCategoryTypeImage != null) {
                Image(
                    painter = painterResource(newCategoryTypeImage!!),
                    contentDescription = newCategoryType!!.name,
                    modifier = Modifier.height(40.dp)
                )
            }
            Text(
                text = newCategoryType?.title ?: "選択する",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun CategoryTypeDropDown(
    categoryTypeMenuExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onTypeClicked:(CategoryTypeEnum) -> Unit
) {
    Box{
        DropdownMenu(
            expanded = categoryTypeMenuExpanded,
            onDismissRequest = onDismissRequest
        ) {
            DropdownMenuItem(
                text = { Text("なし") },
                onClick = onDismissRequest
            )
            CategoryTypeEnum.entries.forEach { type ->

                val categoryTypeImage: Int = getCategoryTypeImage(type)

                DropdownMenuItem(
                    text = {
                        CategoryTypeRow(
                            categoryTypeEnum = type,
                            categoryTypeImage = categoryTypeImage
                        )
                    },
                    onClick = {
                        onTypeClicked(type)
                        onDismissRequest()
                    }
                )
            }
        }
    }
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