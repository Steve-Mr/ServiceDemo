package com.example.servicedemo

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class ExampleTileService : TileService() {

    var isClicked = false

    override fun onClick() {
        super.onClick()
        val tile = qsTile

        val intent = Intent(this, ExampleService::class.java)

        if (!ExampleService.isExampleServiceRunning()){
            applicationContext.startForegroundService(intent)
            tile.state = Tile.STATE_ACTIVE
            isClicked = true
        }else{
            applicationContext.stopService(intent)
            tile.state = Tile.STATE_INACTIVE
            isClicked = false
        }
        tile.updateTile()
    }

//    override fun onStartListening() {
//        super.onStartListening()
//        val tile = qsTile
//
//        tile.state = Tile.STATE_ACTIVE
//        tile.updateTile()
//
//    }

    fun isServiceRunning(serviceClassName: String) : Boolean{
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE).any { it.service.className == serviceClassName }
    }

}