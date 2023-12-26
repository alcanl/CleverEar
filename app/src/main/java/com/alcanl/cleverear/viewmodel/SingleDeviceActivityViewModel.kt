package com.alcanl.cleverear.viewmodel

import com.alcanl.cleverear.SingleDeviceActivity
import java.lang.ref.WeakReference

class SingleDeviceActivityViewModel(activity: SingleDeviceActivity) {
    private val mWeakReference = WeakReference(activity)

    fun handleDetectDeviceButton()
    {
        mWeakReference.get()?.buttonDetectDevice()
    }
    fun handleBackButton()
    {
        mWeakReference.get()?.buttonBack()
    }
}