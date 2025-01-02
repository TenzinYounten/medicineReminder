package com.example.medicinereminder

import android.app.Application
import androidx.room.Room
import com.example.medicinereminder.data.database.AppDatabase
import com.example.medicinereminder.data.repository.MedicineRepository
import com.example.medicinereminder.data.repository.ScheduleRepository

class MedicineReminderApp : Application() {

    private lateinit var database: AppDatabase
    lateinit var medicineRepository: MedicineRepository
    lateinit var scheduleRepository: ScheduleRepository

    override fun onCreate() {
        super.onCreate()
        instance = this
        initializeDatabase()
        initializeRepositories()
    }

    private fun initializeDatabase() {
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "medicine_reminder.db"
        ).build()
    }

    private fun initializeRepositories() {
        medicineRepository = MedicineRepository(database.medicineDao())
        scheduleRepository = ScheduleRepository(database.scheduleDao())
    }

    companion object {
        private lateinit var instance: MedicineReminderApp

        fun getInstance(): MedicineReminderApp = instance
    }
}