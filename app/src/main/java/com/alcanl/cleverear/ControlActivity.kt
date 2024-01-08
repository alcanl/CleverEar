package com.alcanl.cleverear

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import com.alcanl.cleverear.databinding.ActivityControlBinding
import com.alcanl.cleverear.entity.HearingAid
import com.alcanl.cleverear.helpers.EarSide
import com.alcanl.cleverear.helpers.HEARING_AID
import com.alcanl.cleverear.sdk.events.ConnectionStateChangedEvent
import com.alcanl.cleverear.viewmodel.ControlActivityListenersViewModel
import com.ark.ArkException
import com.ark.CommunicationAdaptor
import com.ark.EventHandler
import com.ark.EventType
import com.ark.ParameterSpace
import com.ark.ProductManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import kotlin.concurrent.thread

@AndroidEntryPoint
class ControlActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityControlBinding
    @Inject
    lateinit var mProductManager: ProductManager
    @Inject
    lateinit var mEventHandler: EventHandler
    @Inject
    lateinit var mExecutorService: ExecutorService
    @Volatile
    private var isConnected = false;
    private lateinit var mHearingAid: HearingAid
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        initDevice()
        initialize()
        startConnectionListener()
    }
    override fun onStart()
    {
        super.onStart()
        connectToDevice()
        //readDeviceConfiguration()
        //initMemoryButtons()
    }
    private fun initBinding()
    {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_control)
        mBinding.viewModel = ControlActivityListenersViewModel(this)
        mBinding.isDualDevice = false
        mBinding.hearingAid = mHearingAid
    }
    private fun initialize()
    {
        initBinding()
    }
    private fun initDevice()
    {
        mHearingAid = when {
            VERSION.SDK_INT < VERSION_CODES.TIRAMISU -> intent.getSerializableExtra(HEARING_AID) as HearingAid
            else -> intent.getSerializableExtra(HEARING_AID, HearingAid::class.java)!!
        }
    }
    private fun connectToDevice()
    {
        mHearingAid.communicationAdaptor = mProductManager.createWirelessCommunicationInterface(mHearingAid.address)
        mHearingAid.communicationAdaptor?.setEventHandler(mEventHandler)
        mProductManager.wirelessControl.setCommunicationAdaptor(mHearingAid.communicationAdaptor)

        try {
            mHearingAid.communicationAdaptor?.connect()
        }
        catch (ex: ArkException) {
            Log.e("Error", ex.toString())
        }
        isConnected = true

    }
    private fun readDeviceConfiguration()
    {
        mHearingAid.batteryLevel = mProductManager.wirelessControl.batteryLevel
        mHearingAid.volume = mProductManager.wirelessControl.volume
        mHearingAid.numberOfMemories = mProductManager.wirelessControl.numberOfMemories
        mHearingAid.currentMemory = mProductManager.wirelessControl.currentMemory

    }
    private fun startConnectionListener()
    {
       thread(isDaemon = false, start = true) {
            while (true) {
                val event = mProductManager.eventHandler.event
                if (event.type == EventType.kConnectionEvent) {
                    Log.e("event", event.data)
                    Log.e(ConnectionStateChangedEvent::class.java.simpleName, event.data)
                }
            }
        }
    }
    private fun initMemoryButtons()
    {
        for (i in 1..mHearingAid.numberOfMemories)
            mBinding.tableLayout[i].isEnabled = true
    }
    fun buttonOneClicked()
    {
        mProductManager.wirelessControl.currentMemory = ParameterSpace.kNvmMemory1
    }
    fun buttonTwoClicked()
    {
        mProductManager.wirelessControl.currentMemory = ParameterSpace.kNvmMemory2
    }
    fun buttonThreeClicked()
    {
        mProductManager.wirelessControl.currentMemory = ParameterSpace.kNvmMemory3
    }
    fun buttonFourClicked()
    {
        mProductManager.wirelessControl.currentMemory = ParameterSpace.kNvmMemory4
    }
    fun buttonFiveClicked()
    {
        mProductManager.wirelessControl.currentMemory = ParameterSpace.kNvmMemory5
    }
    fun buttonSixClicked()
    {
        mProductManager.wirelessControl.currentMemory = ParameterSpace.kNvmMemory6
    }
    fun buttonSevenClicked()
    {
        mProductManager.wirelessControl.currentMemory = ParameterSpace.kNvmMemory7
    }
    fun buttonEightClicked()
    {
        mProductManager.wirelessControl.currentMemory = ParameterSpace.kNvmMemory8
    }
    fun sliderLeftEarOnValueChanged(value: Float)
    {
        mProductManager.wirelessControl.volume = value.toInt()

        if (mBinding.isDualDevice)
            sliderRightEarOnValueChanged(value)

    }
    fun sliderRightEarOnValueChanged(value: Float)
    {
        mProductManager.wirelessControl.volume = value.toInt()
    }
    fun switchOnCheckedChanged()
    {
        if (mBinding.isDualDevice) {
            mBinding.sliderLeft.isEnabled = true
            mBinding.sliderRight.isEnabled = false
        }
        else {
            mBinding.sliderLeft.isEnabled = mHearingAid.side == EarSide.Left
            mBinding.sliderRight.isEnabled = mHearingAid.side == EarSide.Right
        }
    }
}