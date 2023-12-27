package com.alcanl.cleverear

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private var mBluetoothPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
    {isGranted : Boolean -> if (isGranted)
    {
        val bluetoothManager = ContextCompat.getSystemService(this, BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager?.adapter
        mBluetoothPermission = true
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            mActivityResultLauncher.launch(enableBluetoothIntent)
        }
        else
            scanDeviceWithBluetooth()

    }
    else
        mBluetoothPermission = false
    }
    private val mActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        result : ActivityResult ->
        if (result.resultCode == RESULT_OK)
            scanDeviceWithBluetooth()
    }
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
    private fun scanDeviceWithBluetooth()
    {
        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
    }

    private fun scanBluetooth()
    {
        val bluetoothManager = ContextCompat.getSystemService(this, BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager?.adapter

        if (bluetoothAdapter == null)
            Toast.makeText(this, R.string.toast_bluetooth_not_found_text, Toast.LENGTH_LONG).show()
        else
            if (VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mBluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                mBluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN)
            }
            else {
                mBluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH_ADMIN)
            }
    }

    fun buttonDetectDevice()
    {
        scanBluetooth()

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
            asyncResult.getResult()
        } catch (e: ArkException) {
            Log.e("",e.message!!)
        }
    }
    fun buttonBack()
    {
        finish()
    }
}