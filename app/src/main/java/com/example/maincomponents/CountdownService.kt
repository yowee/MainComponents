package com.example.maincomponents

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log


class CountdownService : Service() {
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")

    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand")
        val totalTimeMillis = intent?.getLongExtra(EXTRA_TIME, 0) ?: 0
        countDownTimer = object : CountDownTimer(totalTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                Log.d(TAG, "Seconds remaining: $seconds")
            }

            override fun onFinish() {
                Log.d(TAG, "Countdown finished")
                stopSelf() // Stop the service when the countdown finishes
            }

        }
        // Start the countdown timer
        countDownTimer.start()

        // Return START_STICKY to indicate that the service should be restarted if it's killed by the system
        return START_STICKY
    }

    companion object {
        private const val TAG = "CountdownService"
        private const val EXTRA_TIME = "extra_time"

        // Helper method to start the service with the specified countdown time
        fun startServiceWithTime(context: Context, timeMillis: Long) {
            val intent = Intent(context, CountdownService::class.java)
            intent.putExtra(EXTRA_TIME, timeMillis)
            context.startService(intent)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service onDestroy")
        countDownTimer.cancel() // Cancel the countdown timer when the service is destroyed

    }
}



