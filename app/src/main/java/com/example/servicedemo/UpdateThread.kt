package com.example.servicedemo

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.selects.select
import java.security.Permissions
import java.util.*
import java.util.concurrent.TimeUnit

class UpdateThread(exampleService: ExampleService) : Thread() {
    var shutdown = false;
    private var context:Context = exampleService
    private val receivers: MutableList<BroadcastReceiver> = mutableListOf()

    override fun run(){
        var shouldUpdate = context.getSystemService<PowerManager>()?.isInteractive ?:true

        val screenStatus : Intent? =IntentFilter(Intent.ACTION_SCREEN_ON).let { intentFilter ->
            context.registerReceiver(null, intentFilter)
        }

//        if (context.getSystemService<PowerManager>()?.isInteractive != false){

            Timer().schedule(object : TimerTask(){
                override fun run() {
                    Log.v("INTERACTIVE", context.getSystemService<PowerManager>()?.isInteractive.toString())
                    if (context.getSystemService<PowerManager>()?.isInteractive != false){

                    updateNotificationInfo(context)
                    }
                }
            }, 0, 5000L)
//        }

    }

    companion object fun shutdown() {
        this.shutdown = true;
    }

    private fun updateNotificationInfo(context: Context) {
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)// 1

        val batteryManager: BatteryManager =
            context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val currentNow: Long = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)

        val notification = NotificationCompat.Builder(context, "channel 0")
            .setOngoing(true)
            .setSmallIcon(androidx.core.R.drawable.notification_bg)
            .setShowWhen(false)
            .setContentTitle("foreground service alt")
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setOnlyAlertOnce(true)
            .setContentText(currentNow.toString())
            .setContentIntent(pendingIntent)
            .build()

        Log.v("THREAD", "running thread")
        val notificationManager:NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1,notification)
    }
}