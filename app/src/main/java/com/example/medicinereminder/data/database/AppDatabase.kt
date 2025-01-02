package com.example.medicinereminder.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.medicinereminder.data.converter.Converters
import com.example.medicinereminder.data.dao.MedicineDao
import com.example.medicinereminder.data.dao.ScheduleDao
import com.example.medicinereminder.data.entity.Medicine
import com.example.medicinereminder.data.entity.Schedule

@Database(
    entities = [Medicine::class, Schedule::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medicine_reminder_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}