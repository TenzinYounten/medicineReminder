package com.example.medicinereminder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.medicinereminder.ui.screens.medicine.AddMedicineScreen
import com.example.medicinereminder.ui.screens.medicine.MedicineListScreen
import com.example.medicinereminder.viewmodel.MedicineViewModel

sealed class Screen(val route: String) {
    data object MedicineList : Screen("medicineList")
    data object AddMedicine : Screen("addMedicine")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    medicineViewModel: MedicineViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MedicineList.route
    ) {
        composable(Screen.MedicineList.route) {
            MedicineListScreen(
                medicineViewModel = medicineViewModel,
                onAddClick = {
                    navController.navigate(Screen.AddMedicine.route)
                }
            )
        }
        composable(Screen.AddMedicine.route) {
            AddMedicineScreen(
                medicineViewModel = medicineViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}