package com.example.medicinereminder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.medicinereminder.ui.screens.medicine.AddMedicineScreen
import com.example.medicinereminder.ui.screens.medicine.EditMedicineScreen
import com.example.medicinereminder.ui.screens.medicine.MedicineListScreen
import com.example.medicinereminder.viewmodel.MedicineViewModel
sealed class Screen(val route: String) {
    data object MedicineList : Screen("medicineList")
    data object AddMedicine : Screen("addMedicine")
    data object EditMedicine : Screen("editMedicine/{medicineId}") {
        fun createRoute(medicineId: Long) = "editMedicine/$medicineId"
    }
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
                },
                onEditClick = { medicineId ->
                    navController.navigate(Screen.EditMedicine.createRoute(medicineId))
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
        composable(
            route = Screen.EditMedicine.route,
            arguments = listOf(
                navArgument("medicineId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val medicineId = backStackEntry.arguments?.getLong("medicineId") ?: return@composable
            EditMedicineScreen(
                medicineId = medicineId,
                medicineViewModel = medicineViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}