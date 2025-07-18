package com.example.paymentnotification.service

import android.content.Context
import com.example.paymentnotification.data.AppDatabase
import com.example.paymentnotification.data.Payment
import io.nats.client.Connection
import io.nats.client.Nats
import io.nats.client.Options
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

class NatsManager(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private var natsConnection: Connection? = null

    fun connect(url: String, port: String, user: String, pass: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val options = Options.Builder()
                    .server("nats://$url:$port")
                    .userInfo(user, pass)
                    .build()
                natsConnection = Nats.connect(options)
                // Start a background task to upload unsynced payments
                uploadUnsyncedPayments()
            } catch (e: Exception) {
                // TODO: Log error
            }
        }
    }

    private suspend fun uploadUnsyncedPayments() {
        withContext(Dispatchers.IO) {
            val unsyncedPayments = db.paymentDao().getUnsyncedPayments()
            for (payment in unsyncedPayments) {
                publishPayment(payment)
            }
        }
    }

    fun publishPayment(payment: Payment) {
        natsConnection?.let {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val message = "Amount: ${payment.amount}, Payer: ${payment.payer}, Remarks: ${payment.remarks}, Timestamp: ${payment.timestamp}"
                    it.publish("payments", message.toByteArray(StandardCharsets.UTF_8))
                    db.paymentDao().markAsUploaded(payment.id)
                } catch (e: Exception) {
                    // TODO: Log error
                }
            }
        }
    }

    fun disconnect() {
        natsConnection?.close()
    }
}
