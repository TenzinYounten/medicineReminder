package com.example.medicinereminder

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.medicinereminder.ui.navigation.NavGraph
import com.example.medicinereminder.ui.theme.MedicineReminderTheme
import com.example.medicinereminder.viewmodel.MedicineViewModel
import com.example.medicinereminder.viewmodel.ScheduleViewModel
import com.example.medicinereminder.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModelFactory by lazy {
        ViewModelFactory(
            (application as MedicineReminderApp).medicineRepository,
            (application as MedicineReminderApp).scheduleRepository,
            (application as MedicineReminderApp).alarmScheduler
        )
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // Handle permission denial
        }
    }

    private val medicineViewModel: MedicineViewModel by viewModels { viewModelFactory }
    private val scheduleViewModel: ScheduleViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            MedicineReminderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        medicineViewModel = medicineViewModel,
                        scheduleViewModel = scheduleViewModel
                    )
                }
            }
        }
    }
}