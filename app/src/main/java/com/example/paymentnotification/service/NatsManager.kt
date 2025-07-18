package com.example.paymentnotification.service

import android.content.Context
import com.example.paymentnotification.data.AppDatabase
import com.example.paymentnotification.data.Payment
import io.nats.client.*
import io.nats.client.api.PublishAck
import kotlinx.coroutines.*
import java.nio.charset.StandardCharsets
import java.time.Duration

class NatsManager(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private var natsConnection: Connection? = null
    private var jetStream: JetStream? = null
    private var sub1: JetStreamSubscription? = null
    private var sub2: JetStreamSubscription? = null

    fun connect(url: String, port: String, user: String, pass: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val options = Options.Builder()
                    .server("nats://$url:$port")
                    .userInfo(user, pass)
                    .build()
                natsConnection = Nats.connect(options)
                jetStream = natsConnection?.jetStream()
                sub1 = jetStream?.subscribe("payments1")
                sub2 = jetStream?.subscribe("payments2")
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
        jetStream?.let { js ->
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val message = "Amount: ${payment.amount}, Payer: ${payment.payer}, Remarks: ${payment.remarks}, Timestamp: ${payment.timestamp}"
                    val msgId = payment.id.toString()
                    val msg = Message.builder()
                        .data(message.toByteArray(StandardCharsets.UTF_8))
                        .header("Nats-Msg-Id", msgId)
                        .build()


                    js.publish("payments1", msg)
                    js.publish("payments2", msg)

                    var ack1 = false
                    var ack2 = false

                    while (!ack1 || !ack2) {
                        val m1 = sub1?.nextMessage(Duration.ofSeconds(1))
                        if (m1 != null && m1.getHeaders() != null && m1.getHeaders().getFirst("Nats-Msg-Id") == msgId) {
                            ack1 = true
                            m1.ack()
                        }

                        val m2 = sub2?.nextMessage(Duration.ofSeconds(1))
                        if (m2 != null && m2.getHeaders() != null && m2.getHeaders().getFirst("Nats-Msg-Id") == msgId) {
                            ack2 = true
                            m2.ack()
                        }
                    }

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
