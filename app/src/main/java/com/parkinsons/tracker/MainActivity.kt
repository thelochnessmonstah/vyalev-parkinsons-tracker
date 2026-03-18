package com.parkinsons.tracker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        textView.text = "Vyalev Tracker\n\nApp started successfully.\n\nIf you see this, the crash is fixed!"
        textView.textSize = 18f
        textView.setPadding(40, 40, 40, 40)
        setContentView(textView)
    }
}
