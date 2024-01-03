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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.alcanl.cleverear.databinding.ActivitySingleDeviceBinding
import com.alcanl.cleverear.entity.DiscoveredDevice
import com.alcanl.cleverear.entity.HearingAid
import com.alcanl.cleverear.helpers.EarSide
import com.alcanl.cleverear.helpers.MANUFACTURER_CODE
import com.alcanl.cleverear.helpers.parseJsonData
import com.alcanl.cleverear.sdk.events.ScanEvent
import com.alcanl.cleverear.viewmodel.SingleDeviceActivityViewModel
import com.ark.ArkException
import com.ark.AsyncResult
import com.ark.CommunicationPort
import com.ark.EventType
import com.ark.ProductManager
import com.ark.WirelessProgrammerType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.concurrent.thread

@AndroidEntryPoint
class SingleDeviceActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySingleDeviceBinding
    @Inject
    lateinit var mProductManager : ProductManager
    @Volatile
    private var isDeviceFound = false
    private lateinit var asyncResult : AsyncResult
    private var mIsConnected = false
    private var mBluetoothPermission = false
    private lateinit var mEarSide : EarSide
    private val bleDeviceList = ArrayList<DiscoveredDevice>()
    private lateinit var mSelectedHearingAid : HearingAid
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
        mBinding.adapter = ArrayAdapter(this, R.layout.listview_item_layout, ArrayList<String>(emptyList()))
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
    private fun isDuplicate(device: DiscoveredDevice): Boolean
    {
        var value = true

        for (deviceWrapper in bleDeviceList)
            if (deviceWrapper.address == device.address) {
                value = false
                break
        }

        return value
    }
    private fun isHearingAid(manufacturerCode: String) : Boolean
    {
        return manufacturerCode == MANUFACTURER_CODE
    }
    private fun scanDeviceWithBluetooth()
    {
        try {
                asyncResult = mProductManager.beginScanForWirelessDevices(
                WirelessProgrammerType.kPlatformDefault, "",
                if (mEarSide == EarSide.Left) CommunicationPort.kLeft else CommunicationPort.kRight,
                "",
                false
                )

        } catch (e: ArkException) {
            Log.e("",e.message!!)
            Toast.makeText(this, "Fail", Toast.LENGTH_LONG).show()
        }

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

        scanDevices()
    }
    fun deviceDTOItemClicked(pos: Int)
    {
        val device = mBinding.adapter!!.getItem(pos)
        val selectedDevice = bleDeviceList.find { it.name == device.toString() }

        if (selectedDevice != null)
            mSelectedHearingAid = HearingAid(selectedDevice.name, selectedDevice.address, selectedDevice.rssi,
            selectedDevice.manufacturerData, mEarSide)
    }
    fun buttonBack()
    {
        finish()
    }
    private fun scanDevices()
    {
        thread(isDaemon = false, start = true) {
            while (!isDeviceFound) {
                val event = mProductManager.eventHandler.event
                if (event.type == EventType.kScanEvent) {
                    Log.e(ScanEvent::class.java.simpleName, event.data)
                    val device = parseJsonData(
                        event.data,
                        this,
                        if (mEarSide == EarSide.Left) EarSide.Left else EarSide.Right
                    )
                    bleDeviceList.add(device)
                    Log.e(String::class.java.simpleName, bleDeviceList.size.toString())
                    if (!isDuplicate(device) && isHearingAid(device.manufacturerData)) {
                        runOnUiThread { mBinding.adapter!!.add(device.name); mBinding.adapter!!.notifyDataSetChanged() }

                    }
                }
            }
        }
    }
    fun stopScanButtonClicked()
    {
        mProductManager.endScanForWirelessDevices(asyncResult)
        mBinding.progressBar.visibility = View.INVISIBLE
        isDeviceFound = true
    }
}