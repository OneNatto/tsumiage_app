package com.example.tsumiageapp.ui.common.functions

//時間・分を全て分にする
fun changeHourAndMinuteToMinute(
    hour: Int?,
    minute: Int?
): Int {
    var newMinute = 0
    if(hour != null) {
        newMinute = 60 * hour
    }
    if(minute != null) {
        newMinute = newMinute.plus(minute)
    }
    return newMinute
}

//分数を時間表記に変更
fun changeMinuteToHourAndMinute(time: Int) : String {
    val hour = time / 60
    val remainingMinute = time % 60

    return when{
        hour > 0 && remainingMinute > 0 -> "${hour}時間${remainingMinute}分"
        hour > 0 -> "${hour}時間"
        else -> "${remainingMinute}分"
    }
}