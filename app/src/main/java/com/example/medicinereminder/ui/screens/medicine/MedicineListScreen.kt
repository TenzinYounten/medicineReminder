package com.example.medicinereminder.ui.screens.medicine

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.medicinereminder.ui.components.common.ErrorScreen
import com.example.medicinereminder.ui.components.common.LoadingScreen
import com.example.medicinereminder.ui.components.common.MedicineCard
import com.example.medicinereminder.ui.state.MedicineUiState
import com.example.medicinereminder.viewmodel.MedicineViewModel

@Composable
fun MedicineListScreen(
    medicineViewModel: MedicineViewModel,
    onAddClick: () -> Unit
) {
    val uiState by medicineViewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Medicine")
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is MedicineUiState.Loading -> {
                LoadingScreen()
            }
            is MedicineUiState.Success -> {
                val medicines = (uiState as MedicineUiState.Success).medicines
                LazyColumn(contentPadding = paddingValues) {
                    items(medicines) { medicine ->
                        MedicineCard(
                            medicine = medicine,
                            onEditClick = { /* TODO: Implement edit */ },
                            onDeleteClick = { medicineViewModel.deleteMedicine(medicine) }
                        )
                    }
                }
            }
            is MedicineUiState.Error -> {
                ErrorScreen(
                    message = (uiState as MedicineUiState.Error).message,
                    onRetry = { medicineViewModel.loadMedicines() }
                )
            }
        }
    }
}