package com.alcanl.cleverear.viewmodel

import com.alcanl.cleverear.ControlActivity
import java.lang.ref.WeakReference

class ControlActivityListenersViewModel(activity: ControlActivity) {
    private var mWeakReference = WeakReference(activity)
    fun handleButtonOneClick()
    {
        mWeakReference.get()?.buttonOneClicked()
    }
    fun handleButtonTwoClick()
    {
        mWeakReference.get()?.buttonTwoClicked()
    }
    fun handleButtonThreeClick()
    {
        mWeakReference.get()?.buttonThreeClicked()
    }
    fun handleButtonFourClick()
    {
        mWeakReference.get()?.buttonFourClicked()
    }

    fun handleButtonFiveClick()
    {
        mWeakReference.get()?.buttonFiveClicked()
    }
    fun handleButtonSixClick()
    {
        mWeakReference.get()?.buttonSixClicked()
    }
    fun handleButtonSevenClick()
    {
        mWeakReference.get()?.buttonSevenClicked()
    }
    fun handleButtonEightClick()
    {
        mWeakReference.get()?.buttonEightClicked()
    }
}