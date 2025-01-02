package com.example.medicinereminder.data.repository

import com.example.medicinereminder.data.dao.MedicineDao
import com.example.medicinereminder.data.entity.Medicine
import kotlinx.coroutines.flow.Flow

class MedicineRepository(
    private val medicineDao: MedicineDao
) {
    fun getAllMedicines(): Flow<List<Medicine>> =
        medicineDao.getAllMedicines()

    fun getActiveMedicines(): Flow<List<Medicine>> =
        medicineDao.getActiveMedicines()

    suspend fun getMedicineById(id: Long): Medicine? =
        medicineDao.getMedicineById(id)

    suspend fun insertMedicine(medicine: Medicine): Long =
        medicineDao.insert(medicine)

    suspend fun updateMedicine(medicine: Medicine) =
        medicineDao.update(medicine)

    suspend fun deleteMedicine(medicine: Medicine) =
        medicineDao.delete(medicine)

    suspend fun insertOrUpdateMedicine(medicine: Medicine): Long {
        return if (medicine.id == 0L) {
            insertMedicine(medicine)
        } else {
            updateMedicine(medicine)
            medicine.id
        }
    }

    companion object {
        @Volatile
        private var instance: MedicineRepository? = null

        fun getInstance(medicineDao: MedicineDao): MedicineRepository {
            return instance ?: synchronized(this) {
                instance ?: MedicineRepository(medicineDao).also { instance = it }
            }
        }
    }
}