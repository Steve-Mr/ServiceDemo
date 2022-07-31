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

class ExampleService : Service() {

    private var timer = Timer()
    private var isTimerRunning = false
    private var isScreenOnReceiver = false
    private var isLevelReceiver = false

    val screenReceiver = ScreenReceiver()
    private val chargingReceiver = ChargingReceiver()
    val levelReceiver = BatteryLevelReceiver()

    companion object {
        private var isExampleServiceRunning = false
        @JvmStatic
        fun isExampleServiceRunning() : Boolean{
            return isExampleServiceRunning
        }
    }

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

        val batteryStatus: Intent? = registerReceiver(null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

        val isInteractive = getSystemService<PowerManager>()?.isInteractive

        Log.v("STATE", isCharging.toString())
        Log.v("STATUS", status.toString() + " " + BatteryManager.BATTERY_STATUS_CHARGING.toString())
        if (isCharging && isInteractive == true){
            startTimerTask()
        }

        isExampleServiceRunning = true

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
        isExampleServiceRunning = false
        unregisterReceiver(chargingReceiver)
        if (isLevelReceiver) unregisterReceiver(levelReceiver)
        if (isScreenOnReceiver) unregisterReceiver(screenReceiver)
    }

    private fun startTimerTask(){
        timer = Timer()
        if (!isTimerRunning){
            isTimerRunning = true
            timer.schedule(object : TimerTask(){
                override fun run() {
                    updateNotificationInfo(1)
                }
            }, 0, 5000L)
        }
    }

    private fun stopTimerTask(){
        if (isTimerRunning){
            timer.cancel()
            timer.purge()
            isTimerRunning = false
        }
    }

    inner class ScreenReceiver: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (
                ("android.intent.action.SCREEN_ON" != p1?.action
                        && "android.intent.action.SCREEN_OFF" != p1?.action)
                || p0?.applicationContext == null) {
                return
            }
            if ("android.intent.action.SCREEN_ON" == p1.action) {
                startTimerTask()
                Log.v("==SCREENON ALT==", "Screen on")
            } else if ("android.intent.action.SCREEN_OFF" == p1.action) {
                Log.v("==SCREENOFF ALT==", "Screen off")
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
                return
            }
            if ("android.intent.action.ACTION_POWER_CONNECTED" == p1.action) {
                registerReceiver(levelReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                isLevelReceiver = true
                if (getSystemService<PowerManager>()?.isInteractive != false){
                    startTimerTask()
                }
                if (!isScreenOnReceiver){
                    val filter = IntentFilter()
                    filter.addAction(Intent.ACTION_SCREEN_ON)
                    filter.addAction(Intent.ACTION_SCREEN_OFF)
                    registerReceiver(screenReceiver, filter)
                    isScreenOnReceiver = true
                }

                Log.v("==CHARGING ALT==", "Screen on")
            } else if ("android.intent.action.ACTION_POWER_DISCONNECTED" == p1.action) {
                Log.v("==DISCHARGED ALT==", "Screen off")
                stopTimerTask()
                if (isScreenOnReceiver){
                    unregisterReceiver(screenReceiver)
                    isScreenOnReceiver = false
                }
                unregisterReceiver(levelReceiver)
                isLevelReceiver =false
            }
        }
    }

    inner class BatteryLevelReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val level: Int? = p1?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            if (level != null) {
                if (level == 47 || level == 50){
                    updateNotificationInfo(2)
                }
            }
        }
    }

    private fun updateNotificationInfo(id:Int) {
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
        notificationManager.notify(id,notification)
    }


}