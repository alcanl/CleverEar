package com.alcanl.cleverear.helpers

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.alcanl.cleverear.R
import com.alcanl.cleverear.entity.DiscoveredDevice
import org.json.JSONException
import org.json.JSONObject

private const val DEVICE_NAME = 0
private const val DEVICE_MAC = 1
private const val DEVICE_RSSI = 2
const val MANUFACTURING_DATA = 3
const val MANUFACTURER_CODE = "d60900ee01"
const val BUNDLE_EAR_SIDE_KEY = "earSide"
const val RIGHT = "right"
const val LEFT = "left"
const val HEARING_AID = "selected_device"

@Throws(JSONException::class)
fun parseJsonScanData(data: String, context: Context): DiscoveredDevice
{
    val jsonObject = JSONObject(data)
    val scanEvent = jsonObject.getJSONArray(getString(context, R.string.event_json_obj))
    val deviceName = scanEvent.getJSONObject(DEVICE_NAME)
    val deviceMAC = scanEvent.getJSONObject(DEVICE_MAC)
    val deviceRSSI = scanEvent.getJSONObject(DEVICE_RSSI)
    val manufacturingData = scanEvent.getJSONObject(MANUFACTURING_DATA)

    return DiscoveredDevice(
        deviceMAC.getString(getString(context, R.string.device_id_json)),
        deviceName.getString(getString(context, R.string.device_name_json)),
        deviceRSSI.getInt(getString(context, R.string.device_rssi_json)),
        manufacturingData.getString(getString(context, R.string.device_manu_data_json))
    )
}
@Throws(JSONException::class)
fun parseJsonConnectionData(data: String, context: Context) : Int
{
    val jsonObject = JSONObject(data)
    val connectionEvent = jsonObject.getJSONArray(getString(context, R.string.event_json_obj))
    return connectionEvent.getJSONObject(1).getInt(getString(context, R.string.device_connection_state_json))
}
