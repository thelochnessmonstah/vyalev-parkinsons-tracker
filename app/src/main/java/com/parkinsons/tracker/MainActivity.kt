package com.parkinsons.tracker

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var currentState = "OFF"
    private var currentSeverity = "Moderate"
    private var pumpRate = "BASE"
    private var lastBonusTime = 0L

    private lateinit var tvStatus: TextView
    private lateinit var tvLog: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvCurrentStatus)
        tvLog = findViewById(R.id.tvLog)

        setupClickListeners()
        updateStatus()
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.btnOn).setOnClickListener {
            currentState = "ON"
            currentSeverity = ""
            logEntry("ON", "", "Patient is ON")
        }

        findViewById<Button>(R.id.btnMild).setOnClickListener { setOff("Mild") }
        findViewById<Button>(R.id.btnModerate).setOnClickListener { setOff("Moderate") }
        findViewById<Button>(R.id.btnSevere).setOnClickListener { setOff("Severe") }

        findViewById<RadioGroup>(R.id.rgPump).setOnCheckedChangeListener { _, id ->
            pumpRate = when(id) {
                R.id.rbOff -> "OFF"
                R.id.rbLow -> "LOW"
                R.id.rbBase -> "BASE"
                R.id.rbHigh -> "HIGH"
                else -> "BASE"
            }
        }

        findViewById<Button>(R.id.btnBonusDose).setOnClickListener { bonusDose() }
        findViewById<Button>(R.id.btnOralLevo).setOnClickListener { oralLevodopa() }
        findViewById<Button>(R.id.btnAddNote).setOnClickListener { showNoteDialog() }
    }

    private fun setOff(severity: String) {
        currentState = "OFF"
        currentSeverity = severity
        logEntry("OFF", severity, "Patient is OFF - $severity")
        updateStatus()
    }

    private fun bonusDose() {
        val now = System.currentTimeMillis()
        if (now - lastBonusTime < 3600000) {
            Toast.makeText(this, "Bonus dose cooldown is active (1 hour)", Toast.LENGTH_LONG).show()
        } else {
            lastBonusTime = now
            logEntry(currentState, currentSeverity, "Bonus Dose Given")
        }
    }

    private fun oralLevodopa() {
        logEntry(currentState, currentSeverity, "Took Oral Levodopa")
    }

    private fun showNoteDialog() {
        val input = EditText(this).apply { hint = "Enter note..." }
        AlertDialog.Builder(this)
            .setTitle("Add Note")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val text = input.text.toString().trim()
                if (text.isNotEmpty()) logEntry(currentState, currentSeverity, text)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logEntry(state: String, severity: String, note: String) {
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val line = "$time,$state,$severity,$pumpRate,\"$note\""

        try {
            val dir = getExternalFilesDir(null)
            val file = File(dir, "vyalev_log.csv")
            FileWriter(file, true).use { it.append(line).append("\n") }
            Toast.makeText(this, "Logged", Toast.LENGTH_SHORT).show()
            loadRecentLogs()
        } catch (e: Exception) {
            Toast.makeText(this, "Save failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateStatus() {
        val statusText = if (currentState == "ON") "ON" else "OFF - $currentSeverity"
        tvStatus.text = "Current Status: $statusText\nPump: $pumpRate"
    }

    private fun loadRecentLogs() {
        try {
            val file = File(getExternalFilesDir(null), "vyalev_log.csv")
            if (file.exists()) {
                val recent = file.readLines().takeLast(4)
                tvLog.text = "Recent Logs:\n" + recent.joinToString("\n")
            }
        } catch (e: Exception) {}
    }
}
