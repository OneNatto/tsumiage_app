package com.example.tsumiageapp.ui.common.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb


@Composable
fun BoxNumberGrid(
    modifier: Modifier = Modifier,
    taskListSize: Int = 0
) {
    val intervals = generateBoxNumberIntervals(taskListSize)

    val colorScheme = MaterialTheme.colorScheme

    Canvas(modifier = modifier) {
        val intervalHeight = size.height / intervals.size
        intervals.reversed().forEachIndexed { index, label ->
            val y = intervalHeight * (index + 1)

            drawLine(
                color = Color.DarkGray,
                start = Offset(20f,y),
                end = Offset(size.width -20f,y)
            )
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    label,
                    30f,
                    y - 10f,
                    Paint().apply {
                        color = colorScheme.secondary.toArgb()
                        textSize = 30f
                    }
                )
            }
        }
    }
}

fun generateBoxNumberIntervals(taskListSize: Int): List<String> {
    val intervals = mutableListOf<String>()

    var limitNumber = 0

    if(taskListSize < 4) {
        limitNumber = 4
    } else if(taskListSize % 2 == 1) {
        limitNumber = taskListSize + 1
    } else {
        limitNumber = taskListSize + 2
    }

    for (i in 0..limitNumber) {
        if(i != limitNumber) {
            if (i == 0) {
                intervals.add("0")
            } else if (i % 2 == 0) {
                intervals.add("${i}ツミ")
            }
        }else if(limitNumber == 4 && i == 4 ) {
            intervals.add("${i}ツミ")
        }
    }

    return intervals
}