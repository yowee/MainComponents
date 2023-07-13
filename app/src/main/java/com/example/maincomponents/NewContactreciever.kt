package com.example.maincomponents

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NewContactReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ContactsContract.Intents.Insert.ACTION) {
            // A new contact has been added
            val contactName = intent.getStringExtra(ContactsContract.Intents.Insert.NAME)

            // Create a notification
            val builder = NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("New Contact Added")
                .setContentText("Contact Name: $contactName")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            // Show the notification
            val notificationId = 12345 // Choose a unique integer value for the notification ID

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())

        }
    }
}
