package com.alcanl.cleverear.viewmodel

import com.alcanl.cleverear.SingleDeviceActivity
import java.lang.ref.WeakReference

class SingleDeviceActivityListenersViewModel(activity: SingleDeviceActivity) {
    private val mWeakReference = WeakReference(activity)

    fun handleDetectDeviceButton()
    {
        mWeakReference.get()?.buttonDetectDevice()
    }
    fun handleBackButton()
    {
        mWeakReference.get()?.buttonBack()
    }
    fun handleListViewItemSelected(pos: Int)
    {
        mWeakReference.get()?.deviceDTOItemClicked(pos)
    }
    fun handleStopScanButton()
    {
        mWeakReference.get()?.stopScanButtonClicked()
    }
}