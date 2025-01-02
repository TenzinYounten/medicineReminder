package com.example.medicinereminder.ui.screens.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medicinereminder.data.entity.RepeatType
import com.example.medicinereminder.data.entity.Schedule
import com.example.medicinereminder.ui.components.common.TimePickerDialog
import com.example.medicinereminder.viewmodel.ScheduleViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScheduleScreen(
    medicineId: Long,
    scheduleId: Long? = null,
    scheduleViewModel: ScheduleViewModel,
    onNavigateBack: () -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MINUTE)) }
    var selectedRepeatType by remember { mutableStateOf(RepeatType.DAILY) }
    var existingSchedule by remember { mutableStateOf<Schedule?>(null) }

    LaunchedEffect(scheduleId) {
        if (scheduleId != null) {
            val schedule = scheduleViewModel.getScheduleById(scheduleId)
            schedule?.let {
                val calendar = Calendar.getInstance().apply { timeInMillis = it.time }
                selectedHour = calendar.get(Calendar.HOUR_OF_DAY)
                selectedMinute = calendar.get(Calendar.MINUTE)
                selectedRepeatType = it.repeatType
                existingSchedule = it
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (scheduleId == null) "Add Schedule" else "Edit Schedule") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showTimePicker = true }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Time",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = String.format(
                            "%02d:%02d %s",
                            if (selectedHour % 12 == 0) 12 else selectedHour % 12,
                            selectedMinute,
                            if (selectedHour < 12) "AM" else "PM"
                        ),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Repeat",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    RepeatType.values().forEach { repeatType ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedRepeatType == repeatType,
                                onClick = { selectedRepeatType = repeatType }
                            )
                            Text(
                                text = repeatType.name.lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, selectedHour)
                        set(Calendar.MINUTE, selectedMinute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    if (existingSchedule != null) {
                        scheduleViewModel.updateSchedule(
                            existingSchedule!!.copy(
                                time = calendar.timeInMillis,
                                repeatType = selectedRepeatType,
                                nextTriggerTime = calendar.timeInMillis
                            )
                        )
                    } else {
                        scheduleViewModel.addSchedule(
                            medicineId = medicineId,
                            time = calendar.timeInMillis,
                            repeatType = selectedRepeatType,
                            nextTriggerTime = calendar.timeInMillis
                        )
                    }
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (scheduleId == null) "Add Schedule" else "Update Schedule")
            }
        }

        if (showTimePicker) {
            TimePickerDialog(
                initialHour = selectedHour,
                initialMinute = selectedMinute,
                onDismiss = { showTimePicker = false },
                onConfirm = { hour, minute ->
                    selectedHour = hour
                    selectedMinute = minute
                    showTimePicker = false
                }
            )
        }
    }
}