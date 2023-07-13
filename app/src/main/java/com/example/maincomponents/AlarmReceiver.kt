package com.example.maincomponents

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Handle the alarm event
        Toast.makeText(context, "Alarm triggered", Toast.LENGTH_SHORT).show()

        // You can perform any desired actions here
    }
}