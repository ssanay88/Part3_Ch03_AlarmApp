package com.example.part3_ch03_alarmapp

import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.part3_ch03_alarmapp.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        // step0 뷰를 초기화해주기
        initOnOffBtn()
        initChangeAlarmTimeBtn()

        // step1 데이터 가져오기
        // step2 뷰에 데이터를 그려주기

    }

    // 알람 켜고 끄는 버튼 클릭
    private fun initOnOffBtn() {

        mainBinding.onOffBtn.setOnClickListener {
            // 데이터를 확인한다.

            // on / off 에 따라 작업을 처리한다.

            // 오프 -> 알람 제거 , 온 -> 알람 등록

            // 데이터를 저장
        }

    }

    // 알람 시간 변경 버튼 클릭
    private fun initChangeAlarmTimeBtn() {

        mainBinding.changeAlarmTimeBtn.setOnClickListener {
            // 현재 시간을 가져온다 - TimePickerDialog
            val calendar = Calendar.getInstance()   // 현재 시간을 가져온다.

            // 데이터를 저장한다.
            // 뷰를 업데이트 한다.
            // 기존 알람을 삭제 -> 모두 타임피커에서 시간을 선택하여 설정
            TimePickerDialog(this, {picker , hour , minute ->

                val model = saveAlarmModel(hour,minute,false)


            }, calendar.get(Calendar.HOUR_OF_DAY) , calendar.get(Calendar.MINUTE), false).show()

        }

    }

    // 입력한 알람 시간을 sharedPreferences를 이용하여 저장해주는 함수
    private fun saveAlarmModel(
        hour: Int,
        minute: Int,
        onOff: Boolean
    ): AlarmDisplayModel {
        val model = AlarmDisplayModel(
            hour = hour,
            minute = minute,
            onOff = false)

        val sharedPreferences = getSharedPreferences("time", Context.MODE_PRIVATE)
        // with 범위 함수?
        with(sharedPreferences.edit()) {
            putString("alarm", model.makeDataForDB())
            putBoolean("onOff", model.onOff)
            commit()
        }

        return model

    }


}

/*
AlarmManager 사용
Notification 사용
Boadcast receiver 사용

Android에서 Background 작업
백그라운드에서 알람 시간에 맞춰 진행시켜 주도록 한다
 1. Immediate tasks (즉시 실행해야하는 작업)
  - Thread
  - Handler
  - Kotlin coroutines 코틀린에서 지원하는 비동기 프로그래밍 방법

 2. Deferred tasks (지연된 작업) : 앱이 종료되거나 지연되어도 예약된 작업을 진행해야 하는 경우
  - WorkManager

 3. Exact tasks (정시에 실행해야 하는 작업)
  - AlarmManager

 */