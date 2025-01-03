package com.example.medicinereminder.ui.screens.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.medicinereminder.data.entity.RepeatType
import com.example.medicinereminder.data.entity.Schedule
import com.example.medicinereminder.ui.components.common.TimePickerDialog
import com.example.medicinereminder.viewmodel.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScheduleScreen(
    medicineId: Long,
    scheduleId: Long? = null,
    scheduleViewModel: ScheduleViewModel,
    onNavigateBack: () -> Unit
) {
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    var selectedRepeatType by remember { mutableStateOf(RepeatType.DAILY) }
    var numberOfDays by remember { mutableStateOf("7") }

    var startHour by remember { mutableStateOf(8) }  // Default 8 AM
    var startMinute by remember { mutableStateOf(0) }
    var endHour by remember { mutableStateOf(20) }   // Default 8 PM
    var endMinute by remember { mutableStateOf(0) }

    var existingSchedule by remember { mutableStateOf<Schedule?>(null) }

    // Load existing schedule data if editing
    LaunchedEffect(scheduleId) {
        if (scheduleId != null) {
            val schedule = scheduleViewModel.getScheduleById(scheduleId)
            schedule?.let {
                val startCalendar = Calendar.getInstance().apply { timeInMillis = it.dailyStartTime }
                val endCalendar = Calendar.getInstance().apply { timeInMillis = it.dailyEndTime }

                startHour = startCalendar.get(Calendar.HOUR_OF_DAY)
                startMinute = startCalendar.get(Calendar.MINUTE)
                endHour = endCalendar.get(Calendar.HOUR_OF_DAY)
                endMinute = endCalendar.get(Calendar.MINUTE)

                selectedRepeatType = it.repeatType
                numberOfDays = it.numberOfDays.toString()
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
            // Repeat Type Selection
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Repeat Type",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
                                text = when(repeatType) {
                                    RepeatType.HOURLY -> "Every hour"
                                    RepeatType.TWO_HOURLY -> "Every 2 hours"
                                    RepeatType.FOUR_HOURLY -> "Every 4 hours"
                                    RepeatType.DAILY -> "Once daily"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            // Number of Days
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Duration (Days)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = numberOfDays,
                        onValueChange = { numberOfDays = it.filter { char -> char.isDigit() } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Time Window Selection
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Time Window",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Start Time
                    OutlinedButton(
                        onClick = { showStartTimePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Start Time: ${String.format("%02d:%02d %s",
                                if (startHour % 12 == 0) 12 else startHour % 12,
                                startMinute,
                                if (startHour < 12) "AM" else "PM"
                            )}"
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // End Time
                    OutlinedButton(
                        onClick = { showEndTimePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "End Time: ${String.format("%02d:%02d %s",
                                if (endHour % 12 == 0) 12 else endHour % 12,
                                endMinute,
                                if (endHour < 12) "AM" else "PM"
                            )}"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Button(
                onClick = {
                    val startCalendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, startHour)
                        set(Calendar.MINUTE, startMinute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    val endCalendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, endHour)
                        set(Calendar.MINUTE, endMinute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    val schedule = Schedule(
                        id = existingSchedule?.id ?: 0,
                        medicineId = medicineId,
                        time = startCalendar.timeInMillis,
                        repeatType = selectedRepeatType,
                        isActive = true,
                        nextTriggerTime = startCalendar.timeInMillis,
                        lastTriggeredTime = null,
                        numberOfDays = numberOfDays.toIntOrNull() ?: 7,
                        dailyStartTime = startCalendar.timeInMillis,
                        dailyEndTime = endCalendar.timeInMillis,
                        scheduleStartDate = System.currentTimeMillis()
                    )

                    if (existingSchedule != null) {
                        scheduleViewModel.updateSchedule(schedule)
                    } else {
                        scheduleViewModel.addSchedule(
                            medicineId = medicineId,
                            time = startCalendar.timeInMillis,
                            repeatType = selectedRepeatType,
                            nextTriggerTime = startCalendar.timeInMillis,
                            numberOfDays = numberOfDays.toIntOrNull() ?: 7,
                            dailyStartTime = startCalendar.timeInMillis,
                            dailyEndTime = endCalendar.timeInMillis
                        )
                    }
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (scheduleId == null) "Add Schedule" else "Update Schedule")
            }
        }

        if (showStartTimePicker) {
            TimePickerDialog(
                initialHour = startHour,
                initialMinute = startMinute,
                onDismiss = { showStartTimePicker = false },
                onConfirm = { hour, minute ->
                    startHour = hour
                    startMinute = minute
                    showStartTimePicker = false
                }
            )
        }

        if (showEndTimePicker) {
            TimePickerDialog(
                initialHour = endHour,
                initialMinute = endMinute,
                onDismiss = { showEndTimePicker = false },
                onConfirm = { hour, minute ->
                    endHour = hour
                    endMinute = minute
                    showEndTimePicker = false
                }
            )
        }
    }
}