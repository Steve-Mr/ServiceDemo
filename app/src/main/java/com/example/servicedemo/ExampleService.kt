package com.example.servicedemo

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class ExampleService : Service() {

    private val screenReceiver = ScreenReceiver()
    val updateThread = UpdateThread(this)
    val binder = ServiceBinder()


    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE)// 1

        val notification = NotificationCompat.Builder(this, "channel 0")
            .setContentTitle("foreground service")
            .setContentText("service")
            .setSmallIcon(androidx.core.R.drawable.notification_bg)
            .setContentIntent(pendingIntent)
            .build()

        Log.v("STATE", "foreground service")
        startForeground(1, notification)// 2

        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenReceiver, filter)

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
                updateThread.run()
                Log.v("==SCREENON ALT==", "Screen on");
            } else if ("android.intent.action.SCREEN_OFF" == p1.action) {
                Log.v("==SCREENOFF ALT==", "Screen off");

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenReceiver)
    }

    inner class ServiceBinder : Binder(){
        public fun getService() : ExampleService?{
            return this@ExampleService
        }
    }
}