package com.example.medicinereminder.ui.state

import com.example.medicinereminder.data.entity.Schedule

sealed class ScheduleUiState {
    data object Loading : ScheduleUiState()
    data class Success(val schedules: List<Schedule>) : ScheduleUiState()
    data class Error(val message: String) : ScheduleUiState()
}