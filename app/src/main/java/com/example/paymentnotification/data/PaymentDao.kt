package com.example.paymentnotification.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: Payment)

    @Query("SELECT * FROM payments ORDER BY timestamp DESC")
    fun getAllPayments(): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE uploaded = 0 ORDER BY timestamp ASC")
    suspend fun getUnsyncedPayments(): List<Payment>

    @Query("UPDATE payments SET uploaded = 1 WHERE id = :paymentId")
    suspend fun markAsUploaded(paymentId: Int)

    @Query("DELETE FROM payments")
    suspend fun deleteAll()
}
