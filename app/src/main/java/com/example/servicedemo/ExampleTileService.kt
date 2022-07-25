package com.example.servicedemo

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class ExampleTileService : TileService() {

    var isClicked = false

    override fun onClick() {
        super.onClick()

        val tile = qsTile

        val intent = Intent(this, ExampleService::class.java)

        if (!isClicked){
            applicationContext.startForegroundService(intent)
            tile.state = Tile.STATE_INACTIVE
            isClicked = true
        }else{
            applicationContext.stopService(intent)
            tile.state = Tile.STATE_ACTIVE
            isClicked = false
        }
    }

}