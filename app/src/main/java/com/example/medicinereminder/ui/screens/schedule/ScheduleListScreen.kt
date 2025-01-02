package com.example.medicinereminder.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medicinereminder.ui.components.common.ErrorScreen
import com.example.medicinereminder.ui.components.common.LoadingScreen
import com.example.medicinereminder.ui.components.common.ScheduleCard
import com.example.medicinereminder.ui.state.ScheduleUiState
import com.example.medicinereminder.viewmodel.ScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleListScreen(
    medicineId: Long,
    medicineName: String,
    scheduleViewModel: ScheduleViewModel,
    onNavigateBack: () -> Unit,
    onAddSchedule: (Long) -> Unit,
    onEditSchedule: (Long, Long) -> Unit
) {
    val uiState by scheduleViewModel.uiState.collectAsState()

    // Load schedules for this medicine when screen is shown
    LaunchedEffect(medicineId) {
        scheduleViewModel.getSchedulesForMedicine(medicineId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Schedules",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = medicineName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddSchedule(medicineId) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Add Schedule")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
        ) {
            when (uiState) {
                is ScheduleUiState.Loading -> {
                    LoadingScreen()
                }
                is ScheduleUiState.Success -> {
                    val schedules = (uiState as ScheduleUiState.Success).schedules
                    if (schedules.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "No schedules yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap the + button to add a schedule",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                top = 16.dp,
                                bottom = 88.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(schedules) { schedule ->
                                ScheduleCard(
                                    schedule = schedule,
                                    onEditClick = { onEditSchedule(medicineId, schedule.id) },
                                    onDeleteClick = { scheduleViewModel.deleteSchedule(schedule) },
                                    onToggleActive = { scheduleViewModel.toggleScheduleActive(schedule) }
                                )
                            }
                        }
                    }
                }
                is ScheduleUiState.Error -> {
                    ErrorScreen(
                        message = (uiState as ScheduleUiState.Error).message,
                        onRetry = { scheduleViewModel.getSchedulesForMedicine(medicineId) }
                    )
                }
            }
        }
    }
}