package com.example.maincomponents

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast

class NetworkChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            // Network is connected
            Toast.makeText(context, "Network connected", Toast.LENGTH_SHORT).show()
        } else {
            // Network is disconnected
            Toast.makeText(context, "Network disconnected", Toast.LENGTH_SHORT).show()
        }
    }
}
