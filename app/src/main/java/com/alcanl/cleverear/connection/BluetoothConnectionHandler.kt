package com.alcanl.cleverear.connection

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat



class BluetoothConnectionHandler {
    private lateinit var mBluetoothAdapter : BluetoothAdapter

    @RequiresApi(Build.VERSION_CODES.S)
    fun checkForPermission(context : android.content.Context, activity: Activity)
    {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
            if (Build.VERSION.SDK_INT >= 31) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf("Manifest.permission.BLUETOOTH_CONNECT"),
                    100
                )
                return
            }

    }
}