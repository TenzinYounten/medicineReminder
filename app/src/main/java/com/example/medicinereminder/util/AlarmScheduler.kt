package com.example.medicinereminder.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.medicinereminder.receiver.AlarmReceiver
import com.example.medicinereminder.data.entity.RepeatType
import com.example.medicinereminder.data.entity.Schedule
import java.util.*

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(schedule: Schedule, medicineName: String) {
        if (!schedule.isActive) {
            cancelAlarm(schedule.id)
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("scheduleId", schedule.id)
            putExtra("medicineName", medicineName)
        }

        // Calculate end date for the schedule
        val endDate = Calendar.getInstance().apply {
            timeInMillis = schedule.scheduleStartDate
            add(Calendar.DAY_OF_MONTH, schedule.numberOfDays)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.timeInMillis

        // Set up the repeating alarms based on repeat type
        when (schedule.repeatType) {
            RepeatType.HOURLY -> {
                setupHourlyAlarms(schedule, intent, 1, endDate)
            }
            RepeatType.TWO_HOURLY -> {
                setupHourlyAlarms(schedule, intent, 2, endDate)
            }
            RepeatType.FOUR_HOURLY -> {
                setupHourlyAlarms(schedule, intent, 4, endDate)
            }
            RepeatType.DAILY -> {
                setupDailyAlarm(schedule, intent, endDate)
            }
        }
    }

    private fun setupHourlyAlarms(
        schedule: Schedule,
        intent: Intent,
        hourInterval: Int,
        endDate: Long
    ) {
        val currentTime = System.currentTimeMillis()
        val startTimeToday = Calendar.getInstance().apply {
            timeInMillis = schedule.dailyStartTime
            // Set to today's date
            set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
            set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        }

        val endTimeToday = Calendar.getInstance().apply {
            timeInMillis = schedule.dailyEndTime
            // Set to today's date
            set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
            set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        }

        // Calculate first trigger time
        var nextTriggerTime = startTimeToday.timeInMillis
        while (nextTriggerTime <= currentTime || nextTriggerTime > endTimeToday.timeInMillis) {
            if (nextTriggerTime > endTimeToday.timeInMillis) {
                startTimeToday.add(Calendar.DAY_OF_MONTH, 1)
                nextTriggerTime = startTimeToday.timeInMillis
            } else {
                startTimeToday.add(Calendar.HOUR_OF_DAY, hourInterval)
                nextTriggerTime = startTimeToday.timeInMillis
            }
        }

        // Set up individual alarms for each time slot within the day
        var currentSlot = nextTriggerTime
        while (currentSlot <= endDate) {
            if (currentSlot >= schedule.scheduleStartDate &&
                isWithinDailyWindow(currentSlot, schedule.dailyStartTime, schedule.dailyEndTime)) {

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    generateUniqueId(schedule.id, currentSlot),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setAlarmClock(
                            AlarmManager.AlarmClockInfo(currentSlot, pendingIntent),
                            pendingIntent
                        )
                    } else {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            currentSlot,
                            pendingIntent
                        )
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        currentSlot,
                        pendingIntent
                    )
                }
            }

            // Move to next slot
            val calendar = Calendar.getInstance().apply { timeInMillis = currentSlot }
            calendar.add(Calendar.HOUR_OF_DAY, hourInterval)
            currentSlot = calendar.timeInMillis

            // If passed end time, move to next day's start time
            if (!isWithinDailyWindow(currentSlot, schedule.dailyStartTime, schedule.dailyEndTime)) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                calendar.timeInMillis = schedule.dailyStartTime
                calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
                calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                currentSlot = calendar.timeInMillis
            }
        }
    }

    private fun setupDailyAlarm(schedule: Schedule, intent: Intent, endDate: Long) {
        val startTimeToday = Calendar.getInstance().apply {
            timeInMillis = schedule.dailyStartTime
            // Set to today's date
            set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
            set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        }

        var currentDay = startTimeToday.timeInMillis
        while (currentDay <= endDate) {
            if (currentDay >= schedule.scheduleStartDate) {
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    generateUniqueId(schedule.id, currentDay),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setAlarmClock(
                            AlarmManager.AlarmClockInfo(currentDay, pendingIntent),
                            pendingIntent
                        )
                    } else {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            currentDay,
                            pendingIntent
                        )
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        currentDay,
                        pendingIntent
                    )
                }
            }

            // Move to next day
            val calendar = Calendar.getInstance().apply { timeInMillis = currentDay }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            currentDay = calendar.timeInMillis
        }
    }

    private fun isWithinDailyWindow(
        currentTime: Long,
        startTime: Long,
        endTime: Long
    ): Boolean {
        val current = Calendar.getInstance().apply { timeInMillis = currentTime }
        val start = Calendar.getInstance().apply { timeInMillis = startTime }
        val end = Calendar.getInstance().apply { timeInMillis = endTime }

        // Normalize to compare only hours and minutes
        val currentMinutes = current.get(Calendar.HOUR_OF_DAY) * 60 + current.get(Calendar.MINUTE)
        val startMinutes = start.get(Calendar.HOUR_OF_DAY) * 60 + start.get(Calendar.MINUTE)
        val endMinutes = end.get(Calendar.HOUR_OF_DAY) * 60 + end.get(Calendar.MINUTE)

        return currentMinutes in startMinutes..endMinutes
    }

    private fun generateUniqueId(scheduleId: Long, timeInMillis: Long): Int {
        return (scheduleId.toString() + (timeInMillis / 1000 / 60)).hashCode()
    }

    fun cancelAlarm(scheduleId: Long) {
        // Cancel all potential pending intents for this schedule
        val intent = Intent(context, AlarmReceiver::class.java)
        for (i in 0..1000) { // Arbitrary large number to cover all possible alarms
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                generateUniqueId(scheduleId, i.toLong()),
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
        }
    }
}