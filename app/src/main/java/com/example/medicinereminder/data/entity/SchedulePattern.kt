package com.example.medicinereminder.data.entity


data class SchedulePattern(
    val repeatType: RepeatType,
    val numberOfDays: Int,
    val startTime: Long,  // Daily start time (e.g., 8 AM)
    val endTime: Long,    // Daily end time (e.g., 8 PM)
    val startDate: Long   // Start date of the schedule
)