package com.example.medicinereminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.medicinereminder.MedicineReminderApp
import com.example.medicinereminder.data.entity.RepeatType
import com.example.medicinereminder.data.entity.Schedule
import com.example.medicinereminder.util.NotificationHelper
import com.example.medicinereminder.util.ImageStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getLongExtra("scheduleId", -1)

        if (scheduleId != -1L) {
            val app = context.applicationContext as MedicineReminderApp
            val scheduleRepository = app.scheduleRepository
            val medicineRepository = app.medicineRepository
            val notificationHelper = NotificationHelper(context)
            val imageStorage = ImageStorage(context)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val schedule = scheduleRepository.getScheduleById(scheduleId)
                    schedule?.let { currentSchedule ->
                        // Check if the schedule is still within its duration
                        val endDate = Calendar.getInstance().apply {
                            timeInMillis = currentSchedule.scheduleStartDate
                            add(Calendar.DAY_OF_MONTH, currentSchedule.numberOfDays)
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                        }.timeInMillis

                        val currentTime = System.currentTimeMillis()
                        if (currentTime > endDate) {
                            // Schedule has ended, deactivate it
                            scheduleRepository.updateSchedule(currentSchedule.copy(isActive = false))
                            app.alarmScheduler.cancelAlarm(currentSchedule.id)
                            return@launch
                        }

                        // Check if current time is within daily window
                        val isWithinWindow = isWithinDailyWindow(
                            currentTime,
                            currentSchedule.dailyStartTime,
                            currentSchedule.dailyEndTime
                        )

                        if (isWithinWindow) {
                            val medicine = medicineRepository.getMedicineById(currentSchedule.medicineId).firstOrNull()
                            medicine?.let {
                                // Handle medicine image for notification
                                var imageBitmap: Bitmap? = null
                                var imageUri: Uri? = null
                                if (it.imageUri != null) {
                                    imageUri = imageStorage.getImageUri(it.imageUri)
                                    try {
                                        val imageFile = File(it.imageUri)
                                        if (imageFile.exists()) {
                                            imageBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                                // Format time for notification
                                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                                // Show notification with detailed information
                                notificationHelper.showMedicineNotification(
                                    medicine = it,
                                    imageUri = imageUri,
                                    imageBitmap = imageBitmap
                                )

                                // Calculate next trigger time based on repeat type
                                val nextTrigger = calculateNextTrigger(currentSchedule)

                                if (nextTrigger > 0 && nextTrigger <= endDate) {
                                    scheduleRepository.updateSchedule(
                                        currentSchedule.copy(
                                            nextTriggerTime = nextTrigger,
                                            lastTriggeredTime = currentTime
                                        )
                                    )
                                    // Schedule next alarm
                                    app.alarmScheduler.scheduleAlarm(
                                        currentSchedule.copy(nextTriggerTime = nextTrigger),
                                        it.name
                                    )
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun calculateNextTrigger(schedule: Schedule): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = schedule.time

        when (schedule.repeatType) {
            RepeatType.HOURLY -> {
                calendar.add(Calendar.HOUR_OF_DAY, 1)
            }
            RepeatType.TWO_HOURLY -> {
                calendar.add(Calendar.HOUR_OF_DAY, 2)
            }
            RepeatType.FOUR_HOURLY -> {
                calendar.add(Calendar.HOUR_OF_DAY, 4)
            }
            RepeatType.DAILY -> {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // If next trigger is outside daily window, move to next day's start time
        if (!isWithinDailyWindow(calendar.timeInMillis, schedule.dailyStartTime, schedule.dailyEndTime)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            calendar.timeInMillis = schedule.dailyStartTime
            calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
            calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))
            calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        }

        return calendar.timeInMillis
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
}