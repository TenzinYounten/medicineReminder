package com.example.medicinereminder.data.repository

import com.example.medicinereminder.data.dao.MedicineDao
import com.example.medicinereminder.data.dao.ScheduleDao
import com.example.medicinereminder.data.entity.Schedule
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val scheduleDao: ScheduleDao) {

    fun getAllSchedules(): Flow<List<Schedule>> {
        return scheduleDao.getAllSchedules()
    }

    fun getSchedulesForMedicine(medicineId: Long): Flow<List<Schedule>> {
        return scheduleDao.getSchedulesForMedicine(medicineId)
    }

    suspend fun insertSchedule(schedule: Schedule): Long {
        return scheduleDao.insertSchedule(schedule)
    }

    suspend fun updateSchedule(schedule: Schedule) {
        scheduleDao.updateSchedule(schedule)
    }

    suspend fun deleteSchedule(schedule: Schedule) {
        scheduleDao.deleteSchedule(schedule)
    }

    companion object {
        @Volatile
        private var instance: ScheduleRepository? = null

        fun getInstance(scheduleDao: ScheduleDao): ScheduleRepository {
            return instance ?: synchronized(this) {
                instance ?: ScheduleRepository(scheduleDao).also { instance = it }
            }
        }
    }
}