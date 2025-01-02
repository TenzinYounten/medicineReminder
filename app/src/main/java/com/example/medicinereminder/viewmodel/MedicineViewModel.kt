package com.example.medicinereminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinereminder.data.entity.Medicine
import com.example.medicinereminder.data.repository.MedicineRepository
import com.example.medicinereminder.ui.state.MedicineUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicineViewModel(
    private val medicineRepository: MedicineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MedicineUiState>(MedicineUiState.Loading)
    val uiState: StateFlow<MedicineUiState> = _uiState.asStateFlow()

    private val _selectedMedicine = MutableStateFlow<Medicine?>(null)
    val selectedMedicine: StateFlow<Medicine?> = _selectedMedicine.asStateFlow()

    init {
        loadMedicines()
    }

    fun loadMedicines() {
        viewModelScope.launch {
            try {
                medicineRepository.getAllMedicines()
                    .collect { medicines ->
                        _uiState.value = MedicineUiState.Success(medicines)
                    }
            } catch (e: Exception) {
                _uiState.value = MedicineUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun addMedicine(
        name: String,
        imageUri: String?,
        dosage: String,
        instructions: String?,
        startDate: Long,
        endDate: Long?
    ) {
        viewModelScope.launch {
            try {
                val medicine = Medicine(
                    name = name,
                    imageUri = imageUri,
                    dosage = dosage,
                    instructions = instructions,
                    startDate = startDate,
                    endDate = endDate,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                medicineRepository.insertMedicine(medicine)
            } catch (e: Exception) {
                _uiState.value = MedicineUiState.Error(e.message ?: "Failed to add medicine")
            }
        }
    }

    fun updateMedicine(medicine: Medicine) {
        viewModelScope.launch {
            try {
                val updatedMedicine = medicine.copy(updatedAt = System.currentTimeMillis())
                medicineRepository.updateMedicine(updatedMedicine)
            } catch (e: Exception) {
                _uiState.value = MedicineUiState.Error(e.message ?: "Failed to update medicine")
            }
        }
    }

    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch {
            try {
                medicineRepository.deleteMedicine(medicine)
            } catch (e: Exception) {
                _uiState.value = MedicineUiState.Error(e.message ?: "Failed to delete medicine")
            }
        }
    }

    fun selectMedicine(medicine: Medicine) {
        _selectedMedicine.value = medicine
    }

    fun clearSelectedMedicine() {
        _selectedMedicine.value = null
    }
}