package com.example.medicinereminder.data.entity

import androidx.room.*

@Entity(
    tableName = "schedules",
    foreignKeys = [
        ForeignKey(
            entity = Medicine::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("medicineId")]
)
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val medicineId: Long,

    val time: Long,
    val repeatType: RepeatType,
    val isActive: Boolean = true,

    @ColumnInfo(name = "next_trigger_time")
    val nextTriggerTime: Long,

    @ColumnInfo(name = "last_triggered_time")
    val lastTriggeredTime: Long?,

    val numberOfDays: Int,
    val dailyStartTime: Long,
    val dailyEndTime: Long,
    val scheduleStartDate: Long
)
