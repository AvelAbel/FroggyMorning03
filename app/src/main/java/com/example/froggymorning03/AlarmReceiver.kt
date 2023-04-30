package com.example.froggymorning03

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Здесь ваш код для воспроизведения звука
        val mediaPlayer = MediaPlayer.create(context, R.raw.notfffy)
        mediaPlayer.start()

        val alarmTime = intent.getLongExtra("alarmTime", 0L)

        // Открываем AlarmNotificationActivity
        val notificationIntent = Intent(context, AlarmNotificationActivity::class.java)
        notificationIntent.putExtra("alarmTime", alarmTime)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(notificationIntent)

        // Остановите воспроизведение звука после некоторого времени (например, 1 минуты)
        Handler(Looper.getMainLooper()).postDelayed({
            mediaPlayer.stop()
            mediaPlayer.release()
        }, 60000) // 60000 миллисекунд = 1 минута
    }
}
