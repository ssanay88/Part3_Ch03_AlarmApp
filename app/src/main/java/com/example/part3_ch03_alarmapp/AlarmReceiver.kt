package com.example.part3_ch03_alarmapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

// Manifest파일에 등록 필요!!

class AlarmReceiver: BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "1000"
    }

    // 브로드캐스트리시버에서 인텐트가 수신될 경우 실행
    override fun onReceive(context: Context, intent: Intent) {

        createNotificationChannel(context)
        notifyNotification(context)


    }

    private fun createNotificationChannel(context: Context) {

        // SDK 버전이 26이상일 경우
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChaneel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "기상 알람",
                NotificationManager.IMPORTANCE_HIGH,
            )

            NotificationManagerCompat.from(context).createNotificationChannel(notificationChaneel)

        }
    }

    private fun notifyNotification(context: Context) {
        with(NotificationManagerCompat.from(context)) {
            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("알람")
                .setContentText("일어날 시간입니다.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            notify(NOTIFICATION_ID, build.build())
        }
    }


}