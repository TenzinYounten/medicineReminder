package com.example.medicinereminder.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.medicinereminder.data.entity.Medicine

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines ORDER BY name ASC")
    fun getAllMedicines(): Flow<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineById(id: Long): Medicine?

    @Insert
    suspend fun insert(medicine: Medicine): Long

    @Update
    suspend fun update(medicine: Medicine)

    @Delete
    suspend fun delete(medicine: Medicine)

    @Query("SELECT * FROM medicines WHERE end_date >= :currentTime OR end_date IS NULL")
    fun getActiveMedicines(currentTime: Long = System.currentTimeMillis()): Flow<List<Medicine>>
}