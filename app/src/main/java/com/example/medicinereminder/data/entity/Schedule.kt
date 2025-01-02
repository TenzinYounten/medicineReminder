package com.example.medicinereminder.data.entity

import androidx.room.*

@Entity(
    tableName = "schedules",
    foreignKeys = [
        ForeignKey(
            entity = Medicine::class,
            parentColumns = ["id"],
            childColumns = ["medicine_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("medicine_id")]
)
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "medicine_id")
    val medicineId: Long,

    val time: Long,
    val repeatType: RepeatType,
    val isActive: Boolean = true,

    @ColumnInfo(name = "next_trigger_time")
    val nextTriggerTime: Long,

    @ColumnInfo(name = "last_triggered_time")
    val lastTriggeredTime: Long?
)

enum class RepeatType {
    DAILY, WEEKLY, CUSTOM
}