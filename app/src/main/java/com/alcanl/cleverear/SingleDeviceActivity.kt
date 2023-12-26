package com.alcanl.cleverear

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.alcanl.cleverear.databinding.ActivitySingleDeviceBinding
import com.alcanl.cleverear.entity.helpers.EarSide
import com.alcanl.cleverear.viewmodel.SingleDeviceActivityViewModel
import com.ark.ArkException
import com.ark.CommunicationPort
import com.ark.ProductManager
import com.ark.WirelessProgrammerType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SingleDeviceActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySingleDeviceBinding
    @Inject
    lateinit var mProductManager : ProductManager
    private var mIsConnected = false
    private var mBluetoothPermission = false
    private lateinit var mEarSide : EarSide
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        initialize()
        earSideSelectionAlertDialog()
    }
    private fun initialize()
    {
        initBinding()
    }
    private fun initBinding()
    {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_single_device)
        mBinding.viewModel = SingleDeviceActivityViewModel(this)
    }
    private fun earSideSelectionAlertDialog()
    {
        AlertDialog.Builder(this).setCancelable(false)
            .setTitle(R.string.alert_dialog_ear_side_select_title)
            .setMessage(R.string.alert_dialog_ear_side_select_message)
            .setIcon(R.drawable.aid)
            .setPositiveButton(R.string.alert_dialog_left_ear_side_text) {_, _ -> mEarSide = EarSide.Left}
            .setNegativeButton(R.string.alert_dialog_right_ear_side_text) {_,_ -> mEarSide = EarSide.Right}
            .create().show()
    }
    fun buttonDetectDevice()
    {

        if(!mIsConnected)
            mBinding.progressBar.visibility = View.VISIBLE

        try {
            var asyncResult = mProductManager.beginScanForWirelessDevices(
                WirelessProgrammerType.kPlatformDefault,
                "",
                if (mEarSide == EarSide.Left) CommunicationPort.kLeft else CommunicationPort.kRight,
                "",
                false
            )
        } catch (e: ArkException) {
            Log.e("",e.message!!)
        }
    }
    fun buttonBack()
    {
        finish()
    }
}