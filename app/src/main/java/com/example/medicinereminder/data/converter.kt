package com.example.medicinereminder.data.converter

import androidx.room.TypeConverter
import com.example.medicinereminder.data.entity.RepeatType

class Converters {
    @TypeConverter
    fun fromRepeatType(value: RepeatType): String = value.name

    @TypeConverter
    fun toRepeatType(value: String): RepeatType = RepeatType.valueOf(value)
}