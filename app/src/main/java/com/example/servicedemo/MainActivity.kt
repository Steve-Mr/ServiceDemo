package com.example.servicedemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val batteryManager: BatteryManager =
            getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val currentNow: Long = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        val percent : Int = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        Log.d("TAG", currentNow.toString() + "mAh");

        createNotificationChannel();

        val builder = NotificationCompat.Builder(this, "channel 0")
            .setSmallIcon(androidx.core.R.drawable.notification_bg)
            .setContentTitle("notify")
            .setContentText(currentNow.toString() + " " + percent.toString())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(0, builder.build())
            Log.v("state", "supposed to notify")
        }

        val intent = Intent(this, ExampleService::class.java)
        applicationContext.startForegroundService(intent)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = "channel 0"
        val descriptionText = "this is a channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("channel 0", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}