package com.example.tsumiageapp.ui.common.functions


fun getTaskListContentHeightByBoxNumber(
    taskListSize: Int,
    boxHeight: Int
) :Int  {
    var limitNumber = 0
    var taskListHeight = 0

    if(taskListSize < 4) {
        limitNumber = 6
        taskListHeight = boxHeight * limitNumber
    } else if(taskListSize % 2 == 1) {
        limitNumber = taskListSize + 1
        taskListHeight = boxHeight * limitNumber
    }  else {
        limitNumber = taskListSize + 2
        taskListHeight = boxHeight * limitNumber
    }

    return taskListHeight
}