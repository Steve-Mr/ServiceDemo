package com.example.servicedemo

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import java.util.*

class ForegroundService : Service(){

    private var timer = Timer()
    var isTimerRunning = false
    var isScreenOnReceiver = false

    val screenReceiver = ScreenReceiver()
    val chargingReceiver = ChargingReceiver()

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)// 1

        val notification = NotificationCompat.Builder(this, "channel 0")
            .setContentTitle("foreground service")
            .setContentText("service")
            .setSmallIcon(androidx.core.R.drawable.notification_bg)
            .setContentIntent(pendingIntent)
            .build()

        Log.v("STATE", "foreground service")
        startForeground(1, notification)// 2

        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            registerReceiver(null, ifilter)
        }

        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

        val isInteractive = getSystemService<PowerManager>()?.isInteractive

        if (isCharging && isInteractive == true){
            startTimerTask()
        }
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        registerReceiver(chargingReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(chargingReceiver)
    }

    private fun startTimerTask(){
        timer = Timer()
        if (!isTimerRunning){
            isTimerRunning = true
            timer.schedule(object : TimerTask(){
                override fun run() {
                    updateNotificationInfo()
                }
            }, 0, 5000L)
        }
    }

    private fun stopTimerTask(){
        if (isTimerRunning){
            timer.cancel();
            timer.purge();
            isTimerRunning = false;
        }
    }

    inner class ScreenReceiver: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (
                ("android.intent.action.SCREEN_ON" != p1?.action
                        && "android.intent.action.SCREEN_OFF" != p1?.action)
                || p0?.applicationContext == null) {
                return;
            }
            if ("android.intent.action.SCREEN_ON" == p1.action) {
                startTimerTask()
                Log.v("==SCREENON ALT==", "Screen on");
            } else if ("android.intent.action.SCREEN_OFF" == p1.action) {
                Log.v("==SCREENOFF ALT==", "Screen off");
                stopTimerTask()
            }
        }
    }

    inner class ChargingReceiver: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (
                ("android.intent.action.ACTION_POWER_CONNECTED" != p1?.action
                        && "android.intent.action.ACTION_POWER_DISCONNECTED" != p1?.action)
                || p0?.applicationContext == null) {
                return;
            }
            if ("android.intent.action.ACTION_POWER_CONNECTED" == p1.action) {
                if (getSystemService<PowerManager>()?.isInteractive != false){
                    startTimerTask()
                }
                if (!isScreenOnReceiver){
                    val filter = IntentFilter()
                    filter.addAction(Intent.ACTION_SCREEN_ON)
                    filter.addAction(Intent.ACTION_SCREEN_OFF)
                    registerReceiver(screenReceiver, filter)
                }

                Log.v("==CHARGING ALT==", "Screen on");
            } else if ("android.intent.action.ACTION_POWER_DISCONNECTED" == p1.action) {
                Log.v("==DISCHARGED ALT==", "Screen off");
                stopTimerTask()
                if (isScreenOnReceiver){
                    unregisterReceiver(screenReceiver)
                }
            }
        }
    }

    private fun updateNotificationInfo() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)// 1

        val batteryManager: BatteryManager =
            this.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val currentNow: Long = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)

        val notification = NotificationCompat.Builder(this, "channel 0")
            .setOngoing(true)
            .setSmallIcon(androidx.core.R.drawable.notification_bg)
            .setShowWhen(false)
            .setContentTitle("foreground service alt")
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setOnlyAlertOnce(true)
            .setContentText(currentNow.toString())
            .setContentIntent(pendingIntent)
            .build()

        Log.v("THREAD ALT", "running thread")
        val notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1,notification)
    }
}