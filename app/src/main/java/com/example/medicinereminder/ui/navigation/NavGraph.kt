package com.example.medicinereminder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.medicinereminder.ui.screens.medicine.*
import com.example.medicinereminder.ui.screens.schedule.*
import com.example.medicinereminder.viewmodel.*
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object NavRoutes {
    const val MEDICINE_LIST = "medicineList"
    const val ADD_MEDICINE = "addMedicine"
    const val EDIT_MEDICINE = "editMedicine/{medicineId}"
    const val SCHEDULE_LIST = "scheduleList/{medicineId}/{medicineName}"
    const val ADD_SCHEDULE = "addSchedule/{medicineId}"
    const val EDIT_SCHEDULE = "editSchedule/{medicineId}/{scheduleId}"

    fun createEditMedicineRoute(medicineId: Long) = "editMedicine/$medicineId"
    fun createScheduleListRoute(medicineId: Long, medicineName: String): String {
        val encodedName = URLEncoder.encode(medicineName, StandardCharsets.UTF_8.toString())
        return "scheduleList/$medicineId/$encodedName"
    }
    fun createAddScheduleRoute(medicineId: Long) = "addSchedule/$medicineId"
    fun createEditScheduleRoute(medicineId: Long, scheduleId: Long) =
        "editSchedule/$medicineId/$scheduleId"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    medicineViewModel: MedicineViewModel,
    scheduleViewModel: ScheduleViewModel
) {
    NavHost(navController = navController, startDestination = NavRoutes.MEDICINE_LIST) {
        composable(NavRoutes.MEDICINE_LIST) {
            MedicineListScreen(
                medicineViewModel = medicineViewModel,
                onAddClick = { navController.navigate(NavRoutes.ADD_MEDICINE) },
                onEditClick = { medicineId ->
                    navController.navigate(NavRoutes.createEditMedicineRoute(medicineId))
                },
                onSchedulesClick = { medicineId, medicineName ->
                    navController.navigate(NavRoutes.createScheduleListRoute(medicineId, medicineName))
                }
            )
        }

        composable(NavRoutes.ADD_MEDICINE) {
            AddMedicineScreen(
                medicineViewModel = medicineViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.EDIT_MEDICINE,
            arguments = listOf(navArgument("medicineId") { type = NavType.LongType })
        ) { backStackEntry ->
            val medicineId = backStackEntry.arguments?.getLong("medicineId") ?: return@composable
            EditMedicineScreen(
                medicineId = medicineId,
                medicineViewModel = medicineViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.SCHEDULE_LIST,
            arguments = listOf(
                navArgument("medicineId") { type = NavType.LongType },
                navArgument("medicineName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val medicineId = backStackEntry.arguments?.getLong("medicineId") ?: return@composable
            val encodedName = backStackEntry.arguments?.getString("medicineName") ?: return@composable
            val medicineName = URLDecoder.decode(encodedName, StandardCharsets.UTF_8.toString())

            ScheduleListScreen(
                medicineId = medicineId,
                medicineName = medicineName,
                scheduleViewModel = scheduleViewModel,
                onNavigateBack = { navController.popBackStack() },
                onAddSchedule = { id ->
                    navController.navigate(NavRoutes.createAddScheduleRoute(id))
                },
                onEditSchedule = { medId, scheduleId ->
                    navController.navigate(NavRoutes.createEditScheduleRoute(medId, scheduleId))
                }
            )
        }

        composable(
            route = NavRoutes.ADD_SCHEDULE,
            arguments = listOf(navArgument("medicineId") { type = NavType.LongType })
        ) { backStackEntry ->
            val medicineId = backStackEntry.arguments?.getLong("medicineId") ?: return@composable
            AddEditScheduleScreen(
                medicineId = medicineId,
                scheduleId = null,
                scheduleViewModel = scheduleViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.EDIT_SCHEDULE,
            arguments = listOf(
                navArgument("medicineId") { type = NavType.LongType },
                navArgument("scheduleId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val medicineId = backStackEntry.arguments?.getLong("medicineId") ?: return@composable
            val scheduleId = backStackEntry.arguments?.getLong("scheduleId") ?: return@composable
            AddEditScheduleScreen(
                medicineId = medicineId,
                scheduleId = scheduleId,
                scheduleViewModel = scheduleViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}