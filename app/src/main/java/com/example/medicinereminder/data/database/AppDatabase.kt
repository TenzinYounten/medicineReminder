package com.example.medicinereminder.data.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.medicinereminder.data.dao.MedicineDao
import com.example.medicinereminder.data.dao.ScheduleDao
import com.example.medicinereminder.data.entity.Medicine
import com.example.medicinereminder.data.entity.Schedule
import com.example.medicinereminder.data.converter.Converters

@Database(
    entities = [Medicine::class, Schedule::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        private const val DATABASE_NAME = "medicine_reminder.db"

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Recreate medicines table
                database.execSQL("DROP TABLE IF EXISTS medicines")
                database.execSQL("""
                    CREATE TABLE medicines (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        imageUri TEXT,
                        dosage TEXT NOT NULL,
                        instructions TEXT,
                        startDate INTEGER NOT NULL,
                        endDate INTEGER,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)

                // Recreate schedules table
                database.execSQL("DROP TABLE IF EXISTS schedules")
                database.execSQL("""
                    CREATE TABLE schedules (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        medicineId INTEGER NOT NULL,
                        time INTEGER NOT NULL,
                        repeatType TEXT NOT NULL,
                        isActive INTEGER NOT NULL,
                        nextTriggerTime INTEGER NOT NULL,
                        lastTriggeredTime INTEGER,
                        numberOfDays INTEGER NOT NULL,
                        dailyStartTime INTEGER NOT NULL,
                        dailyEndTime INTEGER NOT NULL,
                        scheduleStartDate INTEGER NOT NULL
                    )
                """)
            }
        }

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()  // Just in case migration fails
                .build()
        }
    }
}