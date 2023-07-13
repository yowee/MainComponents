package com.example.maincomponents

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.widget.Toast

class BatteryLevelReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryLevel = level * 100 / scale.toFloat()

            Toast.makeText(context, "Battery level: $batteryLevel%", Toast.LENGTH_SHORT).show()

            // Perform any desired actions based on the battery level
            // For example, you can trigger different behavior for different battery levels
        }
    }
}