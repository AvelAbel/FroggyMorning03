package com.example.froggymorning03

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit

class AlarmNotificationActivity : AppCompatActivity() {

    private lateinit var remainingTimeTextView: TextView
    private lateinit var stopAlarmButton: Button
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_notification)

        remainingTimeTextView = findViewById(R.id.remainingTimeTextView)
        stopAlarmButton = findViewById(R.id.stopAlarmButton)

        val alarmTime = intent.getLongExtra("alarmTime", 0L)
        val remainingTime = calculateRemainingTime(alarmTime)
        startCountdown(remainingTime)

        stopAlarmButton.setOnClickListener {
            // Ваш код для отключения будильника и переноса его на следующий день или на следующую неделю на конкретный день
            // ...
            finish()
        }
    }

    private fun calculateRemainingTime(alarmTime: Long): Long {
        val currentTime = System.currentTimeMillis()
        return alarmTime - currentTime
    }

    private fun startCountdown(remainingTime: Long) {
        countDownTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                remainingTimeTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }

            override fun onFinish() {
                remainingTimeTextView.text = "00:00:00"
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}
