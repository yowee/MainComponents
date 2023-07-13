package com.example.maincomponents

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    companion object {
        private const val TAG = "MyWorker"
    }

    override fun doWork(): Result {
        // Perform the background task
        Log.d(TAG, "Performing background task")

        // Simulate work by delaying for 3 seconds
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        // Return the result
        return Result.success()
    }
}