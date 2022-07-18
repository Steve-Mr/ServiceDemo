package com.example.servicedemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if ("andorid.intent.action.BOOT_COMPLETED".equals(p1?.getAction())){
            Log.d("=boot complete=", "Intent.ACTION_BOOT_COMPLETED");
            val intent = Intent(p0, ExampleService::class.java)
            p0?.startForegroundService(intent)
        }
    }
}