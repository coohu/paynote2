package com.example.paymentnotification.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.paymentnotification.data.PaymentDao
import com.example.paymentnotification.service.NatsManager
import com.example.paymentnotification.util.LogManager
import kotlinx.coroutines.launch

class MainViewModel(
    private val paymentDao: PaymentDao,
    private val natsManager: NatsManager,
    private val logManager: LogManager
) : ViewModel() {

    val payments = paymentDao.getAllPayments().asLiveData()

    fun saveNatsConfig(url: String, port: String, user: String, pass: String) {
        natsManager.connect(url, port, user, pass)
        logManager.log("MainViewModel", "NATS configuration saved")
    }

    fun clearData() {
        viewModelScope.launch {
            paymentDao.deleteAll()
            logManager.log("MainViewModel", "All data cleared")
        }
    }
}
