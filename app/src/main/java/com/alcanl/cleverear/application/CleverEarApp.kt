package com.alcanl.cleverear.application

import android.app.Application
import com.onsemi.androidble.BleUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CleverEarApp : Application(){
    override fun onCreate() {
        super.onCreate()
        BleUtil.BleManager_Init(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        BleUtil.BleManager_DeInit()
    }
}