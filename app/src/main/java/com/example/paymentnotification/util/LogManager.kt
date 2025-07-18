package com.example.paymentnotification.util

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class LogManager(context: Context) {

    private val logDirectory = File(context.getExternalFilesDir(null), "logs")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    init {
        if (!logDirectory.exists()) {
            logDirectory.mkdirs()
        }
    }

    fun log(tag: String, message: String) {
        val calendar = Calendar.getInstance()
        val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        val logFileName = "log_${year}_week_${weekOfYear}.txt"
        val logFile = File(logDirectory, logFileName)

        try {
            val fileWriter = FileWriter(logFile, true)
            val timestamp = timeFormat.format(Date())
            fileWriter.append("$timestamp [$tag] $message\n")
            fileWriter.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
