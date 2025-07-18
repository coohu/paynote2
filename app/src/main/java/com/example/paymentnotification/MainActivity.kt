package com.example.paymentnotification

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.paymentnotification.data.AppDatabase
import com.example.paymentnotification.data.Payment
import com.example.paymentnotification.service.NatsManager
import com.example.paymentnotification.ui.MainViewModel
import com.example.paymentnotification.ui.MainViewModelFactory
import com.example.paymentnotification.ui.theme.PaymentNotificationTheme
import com.example.paymentnotification.util.LogManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaymentNotificationTheme {
                val context = LocalContext.current
                val db = AppDatabase.getDatabase(context)
                val natsManager = NatsManager(context)
                val logManager = LogManager(context)
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(db.paymentDao(), natsManager, logManager)
                )
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val payments by viewModel.payments.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Notification") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Logs", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            LogView(payments)
            Spacer(modifier = Modifier.height(16.dp))
            NatsConfigView(viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            DataManagementView(viewModel)
        }
    }
}

@Composable
fun LogView(payments: List<Payment>) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            context.startActivity(intent)
        }) {
            Text("Request Notification Access")
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            items(payments) { payment ->
                Text("Amount: ${payment.amount}, Payer: ${payment.payer}, Remarks: ${payment.remarks}, Timestamp: ${payment.timestamp}")
            }
        }
    }
}

@Composable
fun NatsConfigView(viewModel: MainViewModel) {
    var natsUrl by remember { mutableStateOf("") }
    var natsPort by remember { mutableStateOf("") }
    var natsUsername by remember { mutableStateOf("") }
    var natsPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("NATS Configuration", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = natsUrl,
            onValueChange = { natsUrl = it },
            label = { Text("NATS URL") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = natsPort,
            onValueChange = { natsPort = it },
            label = { Text("NATS Port") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = natsUsername,
            onValueChange = { natsUsername = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = natsPassword,
            onValueChange = { natsPassword = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.saveNatsConfig(natsUrl, natsPort, natsUsername, natsPassword) }) {
            Text("Save NATS Configuration")
        }
    }
}

@Composable
fun DataManagementView(viewModel: MainViewModel) {
    var selectedDate by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Data Management", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = selectedDate,
            onValueChange = { selectedDate = it },
            label = { Text("Select Date") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { /* TODO: Export data */ }) {
                Text("Export Data")
            }
            Button(onClick = { viewModel.clearData() }) {
                Text("Clear Data")
            }
        }
    }
}
