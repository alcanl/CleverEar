package com.alcanl.cleverear.viewmodel

import com.alcanl.cleverear.EarSideSelectionActivity
import java.lang.ref.WeakReference

class EarSideSelectionActivityListenersViewModel (activity : EarSideSelectionActivity){
    private val mWeakReference = WeakReference(activity)

    fun handleLeftEarButton()
    {
        mWeakReference.get()?.leftEarButtonClicked()
    }
    fun handleRightEarButton()
    {
        mWeakReference.get()?.rightEarButtonClicked()
    }
}