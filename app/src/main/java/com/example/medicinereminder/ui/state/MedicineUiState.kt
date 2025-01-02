package com.example.medicinereminder.ui.state

import com.example.medicinereminder.data.entity.Medicine

sealed class MedicineUiState {
    data object Loading : MedicineUiState()
    data class Success(val medicines: List<Medicine>) : MedicineUiState()
    data class Error(val message: String) : MedicineUiState()
}