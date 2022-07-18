package com.example.servicedemo

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select
import java.util.concurrent.TimeUnit

class DynamicNotificationService() : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private val builder = NotificationCompat.Builder(this, "channel 0")
        .setOngoing(true)
        .setSmallIcon(androidx.core.R.drawable.notification_bg)
        .setShowWhen(false)
        .setContentTitle("foreground service alt")
        .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        .setOnlyAlertOnce(true)
        .setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)
            )

    private fun update(){
        val batteryManager: BatteryManager =
            this.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val currentNow: Long = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)

        val notification = builder
            .setContentText(currentNow.toString())
            .build()

        this.startForeground(1, notification)
    }

    suspend fun run() = coroutineScope{
        var shouldUpdate = getSystemService<PowerManager>()?.isInteractive ?: true
        val ticker = ticker(TimeUnit.SECONDS.toMillis(1))

        while (true){
            select<Unit> {
                if (shouldUpdate){
                    ticker.onReceive{
                        update()
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

}