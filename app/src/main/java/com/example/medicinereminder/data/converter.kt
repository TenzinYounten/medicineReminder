package com.example.medicinereminder.data.converter

import androidx.room.TypeConverter
import com.example.medicinereminder.data.entity.RepeatType

class Converters {
    @TypeConverter
    fun toRepeatType(value: String) = enumValueOf<RepeatType>(value)

    @TypeConverter
    fun fromRepeatType(value: RepeatType) = value.name
}