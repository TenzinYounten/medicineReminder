package com.example.medicinereminder.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medicineId: Long,
    val time: Long,
    val repeatType: RepeatType,
    val isActive: Boolean,
    val nextTriggerTime: Long,
    val lastTriggeredTime: Long?,
    val numberOfDays: Int,
    val dailyStartTime: Long,
    val dailyEndTime: Long,
    val scheduleStartDate: Long
)