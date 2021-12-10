package com.example.part3_ch03_alarmapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

/*
AlarmManager 사용
Notification 사용
Broadcast receiver 사용

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