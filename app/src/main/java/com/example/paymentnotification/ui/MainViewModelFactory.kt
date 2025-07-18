package com.example.paymentnotification.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.paymentnotification.data.PaymentDao
import com.example.paymentnotification.service.NatsManager
import com.example.paymentnotification.util.LogManager

class MainViewModelFactory(
    private val paymentDao: PaymentDao,
    private val natsManager: NatsManager,
    private val logManager: LogManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(paymentDao, natsManager, logManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
