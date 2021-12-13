package com.example.part3_ch03_alarmapp


// data class는 생성자 부분에 인자들을 넣는다.

data class AlarmDisplayModel(
    val hour: Int,
    val minute: Int,
    var onOff: Boolean
) {
    val timeText:String
        // getter함수
        get() {
        val h = "%02d".format(if ( hour < 12 ) hour else hour - 12) // 12
        val m = "%02d".format(minute)

        return "$h:$m"
        }

    val ampmText:String
        get() {
            return if ( hour < 12 ) "AM" else "PM"
        }

    val onOffText:String
        get() {
            return if (onOff == true) "알람 끄기" else "알람 켜기"
        }

    // DB에 저장하기 위해 데이터를 원하는 형태로 가공하는 함수
    fun makeDataForDB(): String {
        return "$hour:$minute"
    }

}
