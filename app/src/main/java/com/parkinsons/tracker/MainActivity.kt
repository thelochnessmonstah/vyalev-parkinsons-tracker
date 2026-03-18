package com.parkinsons.tracker

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvLog: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)

            tvStatus = findViewById(R.id.tvCurrentStatus)
            tvLog = findViewById(R.id.tvLog)

            setupClickListeners()
            updateStatus()
            appendLog("App started successfully")
        } catch (e: Exception) {
            logCrash(e)
            throw e
        }
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.btnOn).setOnClickListener {
            appendLog("Button pressed: ON")
        }

        findViewById<Button>(R.id.btnMild).setOnClickListener { appendLog("Button pressed: Mild") }
        findViewById<Button>(R.id.btnModerate).setOnClickListener { appendLog("Button pressed: Moderate") }
        findViewById<Button>(R.id.btnSevere).setOnClickListener { appendLog("Button pressed: Severe") }

        findViewById<Button>(R.id.btnBonusDose).setOnClickListener { appendLog("Button pressed: Bonus Dose") }
        findViewById<Button>(R.id.btnOralLevo).setOnClickListener { appendLog("Button pressed: Oral Levodopa") }
        findViewById<Button>(R.id.btnAddNote).setOnClickListener {
            appendLog("Add Note button pressed")
            showNoteDialog()
        }
    }

    private fun appendLog(message: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        val line = "$time - $message"

        tvLog.text = line + "\n" + tvLog.text

        try {
            val file = File(getExternalFilesDir(null), "crash_log.txt")
            FileWriter(file, true).use { it.append("$time - $message\n") }
        } catch (e: Exception) {}
    }

    private fun logCrash(e: Exception) {
        try {
            val file = File(getExternalFilesDir(null), "crash_log.txt")
            FileWriter(file, true).use {
                it.append("=== CRASH ===\n")
                it.append("Time: ${Date()}\n")
                it.append("Message: ${e.message}\n")
                it.append("Stack trace:\n")
                e.printStackTrace(PrintWriter(it))
                it.append("================\n\n")
            }
        } catch (ex: Exception) {}
    }

    private fun showNoteDialog() {
        val input = EditText(this).apply { hint = "Enter note..." }
        AlertDialog.Builder(this)
            .setTitle("Add Note")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val text = input.text.toString().trim()
                if (text.isNotEmpty()) appendLog("Note added: $text")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateStatus() {
        tvStatus.text = "Current Status: OFF - Moderate\nPump: BASE"
    }
}
