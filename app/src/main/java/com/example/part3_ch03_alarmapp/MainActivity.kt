package com.example.part3_ch03_alarmapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
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
        // 앱을 시작할때 sharedPreferences에 데이터의 존재 유무와 알람의 onoff를 확인하여 불일치에 대한 예외처리를 한 뒤 AlarmDisplayModel로 반환
        val model = fetchDataFromSharedPreferences()
        renderView(model)    // 해당 데이터를 View에 적용

        // step2 뷰에 데이터를 그려주기

    }

    // 알람 켜고 끄는 버튼 클릭
    private fun initOnOffBtn() {

        mainBinding.onOffBtn.setOnClickListener {
            // 데이터를 확인한다.
            // on / off 에 따라 작업을 처리한다.
            // 오프 -> 알람 제거 , 온 -> 알람 등록
            // 데이터를 저장
            val model = it.tag as? AlarmDisplayModel ?: return@setOnClickListener
            val newModel = saveAlarmModel(model.hour , model.minute, model.onOff.not())
            renderView(newModel)

            if (newModel.onOff) {
                // 새로운 모델이 켜진 경우 -> 알람을 등록
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY , newModel.hour)
                    set(Calendar.MINUTE , newModel.minute)

                    if (before(Calendar.getInstance())) {
                        add(Calendar.DATE , 1)
                    }
                }

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this,AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                // 정시에 실행하고 반복하는 명력
                // Inexcat -> 정확하지 않다 , 즉 정확하진 않지만 반복적으로 알람이 제공되는 명령
                // 해당 메소드는 OS가 잠자기 모드에 들어가도 실행되지않고 정확하지 않기 때문에 현 예제에서 참고용으로만 쓰인다.
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )

            }
            else   // 꺼진 경우 -> 알람을 제거
            {
                cancelAlarm()
            }



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
                renderView(model)    // 뷰를 업데이트
                cancelAlarm()    // 기존 알람을 삭제


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
            onOff = onOff)

        val sharedPreferences = getSharedPreferences("time", Context.MODE_PRIVATE)
        // with 범위 함수?
        with(sharedPreferences.edit()) {
            putString("alarm", model.makeDataForDB())
            putBoolean("onOff", model.onOff)
            commit()
        }

        return model

    }

    // 저장된 데이터를 가져와서 가공하는 함수
    private fun fetchDataFromSharedPreferences():AlarmDisplayModel {

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE)

        // getString에서 null이 반환가능하기 때문에 null일 경우 기본값인 9:30으로 반환하다록 해준다.
        val timeDBValue = sharedPreferences.getString(ALARM_KEY,"9:30") ?: "9:30"
        val onOffDBValue = sharedPreferences.getBoolean(ONOFF_KEY,false)

        // alarm에서 사용할 데이터는 데이터 모델에 저장된 시간형태에서 ':'를 기준으로 앞과 뒤를 구분하여 가져온다.
        val alarmData = timeDBValue.split(":")

        val alarmModel = AlarmDisplayModel(
            hour = alarmData[0].toInt(),
            minute = alarmData[1].toInt(),
            onOff = onOffDBValue
        )

        // 보정 예외 처리 : 알람이 등록된건지 확인해서 처리
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ALARM_REQUEST_CODE ,
            Intent(this, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE    // 펜딩인텐트가 있을 경우만 가져오고 없으면 안받는다.
            )

        // 알람은 꺼져있지만, 데이터는 켜져있는 경우
        if ((pendingIntent == null) and alarmModel.onOff) {
            alarmModel.onOff = false
        }
        // 알람은 켜져있지만, 데이터는 꺼져있는 경우
        else if ((pendingIntent != null) and alarmModel.onOff.not()) {
            pendingIntent.cancel()
        }

        return alarmModel

    }

    // View에 입력된 값을 적용
    private fun renderView(model: AlarmDisplayModel) {
        mainBinding.ampmTextView.text = model.ampmText
        mainBinding.timeTextView.text = model.timeText
        mainBinding.onOffBtn.text = model.onOffText
        // tag?
        mainBinding.onOffBtn.tag = model

    }

    // 등록된 알람을 제거
    private fun cancelAlarm() {
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ALARM_REQUEST_CODE ,
            Intent(this, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE    // 펜딩인텐트가 있을 경우만 가져오고 없으면 안받는다.
        )
        pendingIntent?.cancel()    // 펜딩인텐트 삭제
    }



    // 상수를 지정해주기 위해
    companion object {
        private const val SHARED_PREFERENCES_NAME = "time"
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOff"

        private const val ALARM_REQUEST_CODE = 1000
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