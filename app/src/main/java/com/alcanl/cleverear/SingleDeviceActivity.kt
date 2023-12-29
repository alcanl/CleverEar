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
import com.alcanl.cleverear.dto.BLEDeviceWrapper
import com.alcanl.cleverear.entity.HearingAidModel
import com.alcanl.cleverear.helpers.Configuration
import com.alcanl.cleverear.helpers.EarSide
import com.alcanl.cleverear.viewmodel.SingleDeviceActivityViewModel
import com.ark.ArkException
import com.ark.AsyncResult
import com.ark.CommunicationPort
import com.ark.EventType
import com.ark.ProductManager
import com.ark.WirelessProgrammerType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject
import javax.inject.Inject
import kotlin.concurrent.thread

@AndroidEntryPoint
class SingleDeviceActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySingleDeviceBinding
    @Inject
    lateinit var mProductManager : ProductManager
    @Inject
    lateinit var mConfiguration: Configuration
    @Volatile
    private var isDeviceFound = false
    private lateinit var asyncResult : AsyncResult
    private var mIsConnected = false
    private var mBluetoothPermission = false
    private lateinit var mEarSide : EarSide
    private var bleDeviceList = MutableStateFlow<ArrayList<BLEDeviceWrapper>>(emptyList<BLEDeviceWrapper>() as ArrayList<BLEDeviceWrapper>)
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
        mConfiguration.productManager = mProductManager
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
        bleDeviceList.value.add(BLEDeviceWrapper("test", "test", 0, "test", HearingAidModel.Side.Left))
        mBinding.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, bleDeviceList.value)
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
    private fun isDuplicate(device: BLEDeviceWrapper): Boolean {
        var value = false
        for (deviceWrapper in bleDeviceList.value) {
            value = deviceWrapper.address.equals(device.address)
        }
        return value
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

        test()

    }
    fun deviceDTOItemClicked(pos: Int)
    {
        val device = mBinding.adapter!!.getItem(pos)
        //
    }
    fun buttonBack()
    {
        finish()
    }
    private fun test()
    {

        thread(isDaemon = false, start = true) {
            while (!isDeviceFound) {
                val event = mProductManager.eventHandler.event
                if (event.type == EventType.kScanEvent) {
                    val jsonObject = JSONObject(event.data)
                    val scanEvent = jsonObject.getJSONArray(ContextCompat.getString(this, R.string.event_json_obj))
                    val deviceMACNO = jsonObject.getJSONArray("Event").getJSONObject(0)
                    val deviceName = scanEvent.getJSONObject(0)
                    val deviceMAC = scanEvent.getJSONObject(1)
                    val deviceRSSI = scanEvent.getJSONObject(2)
                    val manufacturingData = scanEvent.getJSONObject(3)
                    val device = BLEDeviceWrapper(
                        deviceMAC.getString(ContextCompat.getString(this, R.string.device_id_json)),
                        deviceName.getString(
                            ContextCompat.getString(
                                this,
                                R.string.device_name_json
                            )
                        ),
                        deviceRSSI.getInt(ContextCompat.getString(this, R.string.device_rssi_json)),
                        manufacturingData.getString(
                            ContextCompat.getString(
                                this,
                                R.string.device_manu_data_json
                            )
                        ),
                        if (mEarSide == EarSide.Left) HearingAidModel.Side.Left else HearingAidModel.Side.Right
                    )
                    for (deviceWrapper in bleDeviceList.value) {
                        if (!deviceWrapper.address.equals(device.address))
                            bleDeviceList.value.add(device)
                            bleDeviceList.value.forEach { runOnUiThread {mBinding.adapter!!.add(it)} }
                    }
                }
            }
        }

        mBinding.adapter!!.notifyDataSetChanged()

    }
    fun stopScanButtonClicked()
    {
        mBinding.progressBar.visibility = View.INVISIBLE
        isDeviceFound = true
    }
}