package com.example.medicinereminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinereminder.data.entity.Schedule
import com.example.medicinereminder.data.entity.RepeatType
import com.example.medicinereminder.data.repository.ScheduleRepository
import com.example.medicinereminder.ui.state.ScheduleUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    private val _selectedSchedule = MutableStateFlow<Schedule?>(null)
    val selectedSchedule: StateFlow<Schedule?> = _selectedSchedule.asStateFlow()

    init {
        loadSchedules()
    }

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
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState.Error(e.message ?: "Failed to add schedule")
            }
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            try {
                scheduleRepository.updateSchedule(schedule)
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState.Error(e.message ?: "Failed to update schedule")
            }
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            try {
                scheduleRepository.deleteSchedule(schedule)
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
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState.Error(e.message ?: "Failed to toggle schedule")
            }
        }
    }

    fun selectSchedule(schedule: Schedule) {
        _selectedSchedule.value = schedule
    }

    fun clearSelectedSchedule() {
        _selectedSchedule.value = null
    }

    fun updateNextTriggerTime(schedule: Schedule, nextTriggerTime: Long) {
        viewModelScope.launch {
            try {
                val updatedSchedule = schedule.copy(
                    nextTriggerTime = nextTriggerTime,
                    lastTriggeredTime = System.currentTimeMillis()
                )
                scheduleRepository.updateSchedule(updatedSchedule)
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState.Error(e.message ?: "Failed to update trigger time")
            }
        }
    }
}