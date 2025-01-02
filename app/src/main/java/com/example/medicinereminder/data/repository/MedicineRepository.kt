package com.example.medicinereminder.data.repository

import com.example.medicinereminder.data.dao.MedicineDao
import com.example.medicinereminder.data.entity.Medicine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

class MedicineRepository(private val medicineDao: MedicineDao) {

    fun getAllMedicines(): Flow<List<Medicine>> {
        return medicineDao.getAllMedicines()
    }

    fun getMedicineById(id: Long): Flow<Medicine> {
        return medicineDao.getMedicineById(id).filterNotNull()
    }

    suspend fun insertMedicine(medicine: Medicine): Long {
        return medicineDao.insertMedicine(medicine)
    }

    suspend fun updateMedicine(medicine: Medicine) {
        medicineDao.updateMedicine(medicine)
    }

    suspend fun deleteMedicine(medicine: Medicine) {
        medicineDao.deleteMedicine(medicine)
    }
}