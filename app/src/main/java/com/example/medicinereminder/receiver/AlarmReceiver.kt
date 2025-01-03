package com.example.medicinereminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medicinereminder.MedicineReminderApp
import com.example.medicinereminder.data.entity.RepeatType
import com.example.medicinereminder.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getLongExtra("scheduleId", -1)
        val medicineName = intent.getStringExtra("medicineName") ?: "Medicine"

        if (scheduleId != -1L) {
            val notificationHelper = NotificationHelper(context)
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val currentTime = timeFormat.format(Date())

            notificationHelper.showNotification(
                "Time for your medicine!",
                "It's time to take $medicineName at $currentTime"
            )

            // Update next trigger time
            val app = context.applicationContext as MedicineReminderApp
            val scheduleRepository = app.scheduleRepository

            CoroutineScope(Dispatchers.IO).launch {
                scheduleRepository.getScheduleById(scheduleId)?.let { schedule ->
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = schedule.time

                    // Set next trigger time based on repeat type
                    val nextTrigger = when (schedule.repeatType) {
                        RepeatType.DAILY -> calendar.apply {
                            add(Calendar.DAY_OF_MONTH, 1)
                        }.timeInMillis
                        RepeatType.WEEKLY -> calendar.apply {
                            add(Calendar.WEEK_OF_YEAR, 1)
                        }.timeInMillis
                        else -> 0L
                    }

                    if (nextTrigger > 0) {
                        scheduleRepository.updateSchedule(
                            schedule.copy(
                                nextTriggerTime = nextTrigger,
                                lastTriggeredTime = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
        }
    }
}