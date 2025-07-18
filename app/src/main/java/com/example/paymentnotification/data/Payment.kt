package com.example.paymentnotification.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val payer: String,
    val remarks: String,
    val timestamp: Long,
    val uploaded: Boolean = false
)
