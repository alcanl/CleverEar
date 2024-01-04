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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.alcanl.cleverear.databinding.ActivitySingleDeviceBinding
import com.alcanl.cleverear.entity.DiscoveredDevice
import com.alcanl.cleverear.entity.HearingAid
import com.alcanl.cleverear.helpers.EXTRA_KEY
import com.alcanl.cleverear.helpers.EarSide
import com.alcanl.cleverear.helpers.LEFT
import com.alcanl.cleverear.helpers.MANUFACTURER_CODE
import com.alcanl.cleverear.helpers.parseJsonData
import com.alcanl.cleverear.sdk.events.ConnectionStateChangedEvent
import com.alcanl.cleverear.sdk.events.ScanEvent
import com.alcanl.cleverear.viewmodel.SingleDeviceActivityListenersViewModel
import com.ark.ArkException
import com.ark.AsyncResult
import com.ark.CommunicationAdaptor
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
    private var mDeviceCount = 0
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
        getEarSideSelection()
    }

    override fun onStart()
    {
        super.onStart()
        buttonDetectDevice()
    }
    private fun initialize()
    {
        initBinding()
    }
    private fun initBinding()
    {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_single_device)
        mBinding.viewModel = SingleDeviceActivityListenersViewModel(this)
        mBinding.adapter = ArrayAdapter(this, R.layout.listview_item_layout, ArrayList<String>(emptyList()))
    }
    private fun getEarSideSelection()
    {
        mEarSide = when {
            intent.getStringExtra(EXTRA_KEY) == LEFT -> EarSide.Left
            else -> EarSide.Right
        }
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
            Toast.makeText(this, "Failed to connecting the device", Toast.LENGTH_LONG).show()
        }

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
        isDeviceFound = false
        scanBluetooth()

        if(!mIsConnected)
            mBinding.progressBar.visibility = View.VISIBLE

        scanDevices()
        mBinding.frameLayoutStopScan.visibility = View.VISIBLE
        mBinding.imageButtonDetect.isEnabled = false
    }
    fun deviceDTOItemClicked(pos: Int)
    {
        isDeviceFound = true
        mBinding.textViewDeviceState.text = getText(R.string.textview_device_detection_connection_in_process_text)


        val device = mBinding.adapter!!.getItem(pos)
        val selectedDevice = bleDeviceList.find { it.name == device.toString() }

        if (selectedDevice != null) {
            mSelectedHearingAid = HearingAid(
                selectedDevice.name, selectedDevice.address, selectedDevice.rssi,
                selectedDevice.manufacturerData, mEarSide
            )

           val test = mProductManager.createWirelessCommunicationInterface(selectedDevice.address)

            test.connect()

        }

    }
    fun buttonBack()
    {
        finish()
    }
    private fun scanDevices()
    {
        bleDeviceList.clear()
        try {
            thread(isDaemon = true, start = true) {
                while (true) {
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
                            runOnUiThread {
                                mBinding.adapter!!.add(device.name); mBinding.adapter!!.notifyDataSetChanged()
                                mBinding.textViewDeviceState.text = String.format(
                                    "%d %s", ++mDeviceCount,
                                    getText(R.string.textview_count_device_found_text)
                                )
                            }
                        }
                    }
                    if (event.type == EventType.kConnectionEvent)
                        Log.e(ConnectionStateChangedEvent::class.java.simpleName, event.data)
                }
            }
        } catch (_ : IndexOutOfBoundsException)
        {
            Toast.makeText(this, "ScanMemoryLeak", Toast.LENGTH_LONG).show()
        }
    }
    fun stopScanButtonClicked()
    {
        mProductManager.endScanForWirelessDevices(asyncResult)
        isDeviceFound = true
        mBinding.imageButtonDetect.isEnabled = true
        mBinding.progressBar.visibility = View.GONE
        mBinding.frameLayoutStopScan.visibility = View.INVISIBLE

    }
}