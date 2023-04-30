package com.example.froggymorning03

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private lateinit var timePicker: TimePicker
    private lateinit var setAlarmButton: Button
    private lateinit var countdownTextView: TextView
    private lateinit var daysOfWeekButtons: List<ToggleButton>
    private var alarmManager: AlarmManager? = null
    private var pendingIntent: PendingIntent? = null
    private var countDownTimer: CountDownTimer? = null

    companion object {
        private const val REQUEST_SCHEDULE_EXACT_ALARM_PERMISSION = 1
        const val ALARM_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timePicker = findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true)

        setAlarmButton = findViewById(R.id.setAlarmButton)
        countdownTextView = findViewById(R.id.countdownTextView)

        daysOfWeekButtons = listOf(
            findViewById(R.id.mondayButton),
            findViewById(R.id.tuesdayButton),
            findViewById(R.id.wednesdayButton),
            findViewById(R.id.thursdayButton),
            findViewById(R.id.fridayButton),
            findViewById(R.id.saturdayButton),
            findViewById(R.id.sundayButton)
        )

        setAlarmButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            setAlarm(calendar)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_SCHEDULE_EXACT_ALARM_PERMISSION && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)
            calendar.set(Calendar.SECOND, 0)
            setAlarm(calendar)
        }
    }


    private fun setAlarm(calendar: Calendar) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
        calendar.set(Calendar.MINUTE, timePicker.minute)
        calendar.set(Calendar.SECOND, 0)

        val selectedDays = daysOfWeekButtons.filter { it.isChecked }.map { it.text.toString() }

        // Отменяем предыдущий будильник
        alarmManager.cancel(pendingIntent)

        calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
        calendar.set(Calendar.MINUTE, timePicker.minute)
        calendar.set(Calendar.SECOND, 0)

        val alarms = mutableListOf<Long>()

        if (selectedDays.isNotEmpty()) {
            for (day in selectedDays) {
                val dayOfWeek = when (day) {
                    "Пн" -> Calendar.MONDAY
                    "Вт" -> Calendar.TUESDAY
                    "Ср" -> Calendar.WEDNESDAY
                    "Чт" -> Calendar.THURSDAY
                    "Пт" -> Calendar.FRIDAY
                    "Сб" -> Calendar.SATURDAY
                    "Вс" -> Calendar.SUNDAY
                    else -> throw IllegalArgumentException("Unknown day: $day")
                }

                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)

                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1)
                }

                alarms.add(calendar.timeInMillis)
            }
        } else {
            if (calendar.timeInMillis < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            alarms.add(calendar.timeInMillis)
        }

        // Берем последний установленный будильник
        val lastAlarm = alarms.last()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM),
                    REQUEST_SCHEDULE_EXACT_ALARM_PERMISSION
                )
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    lastAlarm,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                lastAlarm,
                pendingIntent
            )
        }

        for (button in daysOfWeekButtons) {
            button.isChecked = false
        }

        // Очистите все предыдущие будильники здесь
        // ...

        startCountdown(lastAlarm)
    }





    private fun startCountdown(alarmTimeInMillis: Long) {
        countDownTimer?.cancel()
        val currentTimeInMillis = System.currentTimeMillis()
        val timeLeftInMillis = alarmTimeInMillis - currentTimeInMillis

        val countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60)
                val seconds = (millisUntilFinished % (1000 * 60)) / 1000

                countdownTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }

            override fun onFinish() {
                countdownTextView.text = "00:00:00"
            }
        }

        countDownTimer.start()
    }
}

