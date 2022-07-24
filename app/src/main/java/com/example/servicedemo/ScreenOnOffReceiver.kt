package com.example.servicedemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.SyncStateContract
import android.util.Log

class ScreenOnOffReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (
            ("android.intent.action.SCREEN_ON" != p1?.action
                    && "android.intent.action.SCREEN_OFF" != p1?.action)
            || p0?.applicationContext == null) {
            return;
        }
        if ("android.intent.action.SCREEN_ON" == p1.action) {
            Log.v("==SCREENON==", "Screen on");
        } else if ("android.intent.action.SCREEN_OFF" == p1.action) {
            Log.v("==SCREENOFF==", "Screen off");

        }
    }
}