package com.example.medicinereminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinereminder.data.entity.Schedule
import com.example.medicinereminder.data.entity.RepeatType
import com.example.medicinereminder.data.repository.ScheduleRepository
import com.example.medicinereminder.ui.state.ScheduleUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleViewModel(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    private val _selectedSchedule = MutableStateFlow<Schedule?>(null)
    val selectedSchedule: StateFlow<Schedule?> = _selectedSchedule.asStateFlow()

    fun loadSchedules() {
        viewModelScope.launch {
            try {
                scheduleRepository.getAllSchedules()
                    .collect { schedules ->
                        _uiState.value = ScheduleUiState.Success(schedules)
                    }
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun getSchedulesForMedicine(medicineId: Long) {
        viewModelScope.launch {
            try {
                scheduleRepository.getSchedulesForMedicine(medicineId)
                    .collect { schedules ->
                        _uiState.value = ScheduleUiState.Success(schedules)
                    }
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState.Error(e.message ?: "Failed to load schedules")
            }
        }
    }

    suspend fun getScheduleById(scheduleId: Long): Schedule? {
        return withContext(Dispatchers.IO) {
            scheduleRepository.getScheduleById(scheduleId)
        }
    }

    fun addSchedule(
        medicineId: Long,
        time: Long,
        repeatType: RepeatType,
        nextTriggerTime: Long
    ) {
        viewModelScope.launch {
            try {
                val schedule = Schedule(
                    medicineId = medicineId,
                    time = time,
                    repeatType = repeatType,
                    isActive = true,
                    nextTriggerTime = nextTriggerTime,
                    lastTriggeredTime = null
                )
                scheduleRepository.insertSchedule(schedule)
                getSchedulesForMedicine(medicineId)
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState.Error(e.message ?: "Failed to add schedule")
            }
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            try {
                scheduleRepository.updateSchedule(schedule)
                getSchedulesForMedicine(schedule.medicineId)
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState.Error(e.message ?: "Failed to update schedule")
            }
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            try {
                scheduleRepository.deleteSchedule(schedule)
                getSchedulesForMedicine(schedule.medicineId)
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState.Error(e.message ?: "Failed to delete schedule")
            }
        }
    }

    fun toggleScheduleActive(schedule: Schedule) {
        viewModelScope.launch {
            try {
                val updatedSchedule = schedule.copy(isActive = !schedule.isActive)
                scheduleRepository.updateSchedule(updatedSchedule)
                getSchedulesForMedicine(schedule.medicineId)
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState.Error(e.message ?: "Failed to toggle schedule")
            }
        }
    }

    fun updateNextTriggerTime(schedule: Schedule, nextTriggerTime: Long) {
        viewModelScope.launch {
            try {
                val updatedSchedule = schedule.copy(
                    nextTriggerTime = nextTriggerTime,
                    lastTriggeredTime = System.currentTimeMillis()
                )
                scheduleRepository.updateSchedule(updatedSchedule)
                getSchedulesForMedicine(schedule.medicineId)
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState.Error(e.message ?: "Failed to update trigger time")
            }
        }
    }
}