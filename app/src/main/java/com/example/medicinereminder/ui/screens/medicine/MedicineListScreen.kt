package com.example.medicinereminder.ui.screens.medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medicinereminder.ui.components.common.ErrorScreen
import com.example.medicinereminder.ui.components.common.LoadingScreen
import com.example.medicinereminder.ui.components.common.MedicineCard
import com.example.medicinereminder.ui.state.MedicineUiState
import com.example.medicinereminder.viewmodel.MedicineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineListScreen(
    medicineViewModel: MedicineViewModel,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onSchedulesClick: (Long, String) -> Unit
) {
    val uiState by medicineViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Medicine Reminder",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Medicine")
            }
        }
    ) { padding ->
        when (uiState) {
            is MedicineUiState.Loading -> {
                LoadingScreen()
            }
            is MedicineUiState.Success -> {
                val medicines = (uiState as MedicineUiState.Success).medicines
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5))
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = padding.calculateTopPadding() + 16.dp,
                            bottom = padding.calculateBottomPadding() + 88.dp,
                            start = 16.dp,
                            end = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(medicines) { medicine ->
                            MedicineCard(
                                medicine = medicine,
                                onEditClick = { onEditClick(medicine.id) },
                                onDeleteClick = { medicineViewModel.deleteMedicine(medicine) },
                                onSchedulesClick = { onSchedulesClick(medicine.id, medicine.name) }
                            )
                        }
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