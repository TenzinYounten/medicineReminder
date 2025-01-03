package com.example.medicinereminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicinereminder.data.repository.MedicineRepository
import com.example.medicinereminder.data.repository.ScheduleRepository
import com.example.medicinereminder.util.AlarmScheduler

class ViewModelFactory(
    private val medicineRepository: MedicineRepository,
    private val scheduleRepository: ScheduleRepository,
    private val alarmScheduler: AlarmScheduler

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MedicineViewModel::class.java) -> {
                MedicineViewModel(medicineRepository) as T
            }
            modelClass.isAssignableFrom(ScheduleViewModel::class.java) -> {
                ScheduleViewModel(scheduleRepository,medicineRepository,alarmScheduler) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}