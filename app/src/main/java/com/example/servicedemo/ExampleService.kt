package com.example.servicedemo

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class ExampleService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
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
        val screenReceiver = ScreenOnOffReceiver()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenReceiver, filter)
        val updateThread = UpdateThread(this)
        updateThread.run()
    }
}