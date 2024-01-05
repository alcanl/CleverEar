package com.alcanl.cleverear

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.alcanl.cleverear.databinding.ActivityControlBinding
import com.alcanl.cleverear.entity.HearingAid
import com.alcanl.cleverear.viewmodel.ControlActivityListenersViewModel
import com.ark.EventHandler
import com.ark.ProductManager
import javax.inject.Inject

class ControlActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityControlBinding
    @Inject
    lateinit var mProductManager: ProductManager
    @Inject
    lateinit var mEventHandler: EventHandler
    private lateinit var mHearingAid: HearingAid
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        initialize()
    }
    private fun initBinding()
    {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_control)
        mBinding.viewModel = ControlActivityListenersViewModel(this)
    }
    private fun initialize()
    {
        initBinding()
    }
    fun buttonOneClicked()
    {

    }
    fun buttonTwoClicked()
    {

    }
    fun buttonThreeClicked()
    {

    }
    fun buttonFourClicked()
    {

    }
    fun buttonFiveClicked()
    {

    }
    fun buttonSixClicked()
    {

    }
    fun buttonSevenClicked()
    {

    }
    fun buttonEightClicked()
    {

    }
}