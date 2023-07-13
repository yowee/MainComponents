package com.example.maincomponents

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private val REQUEST_READ_CONTACTS: Int = 1231
    var mobileArray = mutableListOf<String>()
    var numberArray = mutableListOf<String>()
    val TAG = "MAIN_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(TAG, " On on onCreate() This method is called when the activity is first created. ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialize Broadcast
        val filter = IntentFilter()
        filter.addAction("com.example.maincomponents.MainBroadcastReceiver")
        registerReceiver(MainBroadcastReceiver(), filter)

        val intent = Intent("com.example.maincomponents.MainBroadcastReceiver")
        sendBroadcast(intent)


        //Initialize Service
        val button = findViewById<Button>(R.id.btnMusicPlayer)
        button.setOnClickListener {
//            loadContacts()
            if (isMyServiceRunning(MainService::class.java)) {
                button.text = getString(R.string.stopped)
                stopService(Intent(this@MainActivity, MainService::class.java))
            } else {
                button.text = getString(R.string.started)
                startService(Intent(this@MainActivity, MainService::class.java))
            }
        }


        //Initialize Content Providers
        loadContacts()

        // Start the service with a countdown time of 60 seconds (60000 milliseconds)
        CountdownService.startServiceWithTime(this, 60000)

        scheduleWork()

        alarmManager()

        sendNotification()

    }

    private fun sendNotification() {
        val intent = Intent("com.example.MY_NOTIFICATION")
        intent.putExtra("message", "Hello from BroadcastReceiver!")
        sendBroadcast(intent)
    }

    private fun alarmManager() {
        /*   // Get the AlarmManager instance
          val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

           // Create an intent for the AlarmReceiver
           val intent = Intent(this, AlarmReceiver::class.java)
           val flags = PendingIntent.FLAG_UPDATE_CURRENT

           val alarmIntent = PendingIntent.getBroadcast(this, 0, intent, flags)

           // Set the alarm to trigger after 5 seconds
           val triggerTime = System.currentTimeMillis() + 5000
           alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, alarmIntent)
   */
    }

    private fun scheduleWork() {
        // Create a OneTimeWorkRequest for your worker
        val workRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .build()

        // Enqueue the work request with WorkManager
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueue(workRequest)

        // Listen to the work request's status
        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(this, Observer { workInfo ->
                if (workInfo != null) {
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        // Work completed successfully

                        // Perform any desired actions
                    }
                }
            })
    }


    private fun loadContacts() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            var listView: ListView
            var adapter: ContactAdapter

            listView = findViewById(R.id.lvContacts)

            // Get the contact list as a List<Contact>
            val contactList: List<Contact> = getContactList(this)

            // Create a custom adapter to populate the ListView
            adapter = ContactAdapter(this, contactList)

            // Set the adapter to the ListView
            listView.adapter = adapter
        } else {
            requestContactsPermission()
            Toast.makeText(this, "Permission!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestContactsPermission() {
        // Check if the permission has already been granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission has already been granted, do something with the contact list
            val contacts = getContactList(this)
            Log.d("Contacts", contacts.joinToString(separator = "\n"))
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
            // Do something with the contact list
        } else {
            // Permission has not been granted, request it
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                // Explain why the app needs the permission
                // You can show a dialog or a Snackbar here
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "The app needs permission to access your contacts.",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("OK") {
                    // Request the permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        REQUEST_READ_CONTACTS
                    )
                }.show()
            } else {
                // Request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    REQUEST_READ_CONTACTS
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_CONTACTS && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission has been granted, do something with the contact list
            val contacts = getContactList(this)
            Log.d("Contacts", contacts.joinToString(separator = "\n"))
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
            // Do something with the contact list
        } else {
            // Permission has been denied
            // You can show a dialog or a Snackbar here to explain why the app needs the permission
        }
    }

    @SuppressLint("Range")
    fun getContactList(context: Context): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber =
                    it.getInt(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0
                if (hasPhoneNumber) {
                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    phoneCursor?.use { pc ->
                        while (pc.moveToNext()) {
                            val phoneNumber =
                                pc.getString(pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            val contact = Contact(name, phoneNumber)
                            contacts.add(contact)
                        }
                    }
                    phoneCursor?.close()
                } else {
                    val contact = Contact(name, null)
                    contacts.add(contact)
                }
            }
        }
        cursor?.close()
        return contacts
    }

    data class Contact(val name: String, val phoneNumber: String?)


    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, " On  onStart() visible to the user but doesn't have focus yet.")

    }

    override fun onResume() {
        super.onResume()
        Log.d(
            TAG,
            " On  onResme() .. user interacting with th ui, Ui is focused activity is on the top of stack"
        )
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, " On onPause() .. activity no longer in the foreground")

    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, " On onStop() .. activity is no longer visible to the user")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, " On onRestart() .. activity is restarted")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, " On onRestart() .. activity is destroyed and removed from memory.")

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        Log.d(
            TAG,
            " On onRestart() ..         This method is called before the activity is paused or destroyed, allowing you to save any dynamic data or state information that you want to retain."
        )

    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        Log.d(
            TAG,
            "This method is called after onStart() when the activity is being re-initialized from a previously saved state. It allows you to restore the saved data."
        )
    }
}

class ContactAdapter(context: Context, contacts: List<MainActivity.Contact>) :
    ArrayAdapter<MainActivity.Contact>(context, 0, contacts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val holder: ViewHolder

        if (itemView == null) {
            itemView = LayoutInflater.from(context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
            holder = ViewHolder(itemView)
            itemView.tag = holder
        } else {
            holder = itemView.tag as ViewHolder
        }

        val contact = getItem(position)

        holder.nameTextView.text = contact?.name
        holder.phoneNumberTextView.text = contact?.phoneNumber

        return itemView!!
    }

    private class ViewHolder(view: View) {
        val nameTextView: TextView = view.findViewById(android.R.id.text1)
        val phoneNumberTextView: TextView = view.findViewById(android.R.id.text2)
    }
}




