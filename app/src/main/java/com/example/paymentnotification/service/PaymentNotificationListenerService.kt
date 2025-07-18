package com.example.paymentnotification.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.paymentnotification.data.AppDatabase
import com.example.paymentnotification.data.Payment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PaymentNotificationListenerService : NotificationListenerService() {

    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        if (packageName != "com.tencent.mm" && packageName != "com.eg.android.AlipayGphone") {
            return
        }

        val notification = sbn.notification
        val extras = notification.extras
        val title = extras.getString("android.title")
        val text = extras.getCharSequence("android.text")?.toString()

        if (title != null && text != null) {
            parsePaymentNotification(packageName, title, text)
        }
    }

    private fun parsePaymentNotification(packageName: String, title: String, text: String) {
        // TODO: Implement notification parsing logic
        // This is a placeholder implementation
        val amount = 100.0
        val payer = "Unknown"
        val remarks = "No remarks"
        val timestamp = System.currentTimeMillis()

        val payment = Payment(
            amount = amount,
            payer = payer,
            remarks = remarks,
            timestamp = timestamp
        )

        GlobalScope.launch {
            db.paymentDao().insert(payment)
        }
    }
}
