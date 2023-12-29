package com.alcanl.cleverear.helpers

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.alcanl.cleverear.R
import com.alcanl.cleverear.dto.BLEDeviceWrapper
import com.alcanl.cleverear.entity.HearingAidModel
import com.alcanl.cleverear.sdk.events.ScanEvent
import org.json.JSONException
import org.json.JSONObject

private const val DEVICE_NAME = 0
private const val DEVICE_MAC = 1
private const val DEVICE_RSSI = 2
const val MANUFACTURING_DATA = 3

@Throws(JSONException::class)
fun parseJsonData(data: ScanEvent, context: Context): BLEDeviceWrapper
{
    val jsonObject = JSONObject(data.data)
    val scanEvent = jsonObject.getJSONArray(getString(context, R.string.event_json_obj))
    val deviceName = scanEvent.getJSONObject(DEVICE_NAME)
    val deviceMAC = scanEvent.getJSONObject(DEVICE_MAC)
    val deviceRSSI = scanEvent.getJSONObject(DEVICE_RSSI)
    val manufacturingData = scanEvent.getJSONObject(MANUFACTURING_DATA)
    var advertisingSide: HearingAidModel.Side? = null
    val manData = manufacturingData.getString("ManufacturingData")
    val manDataSide = manData.substring(0, 6)
    if (manDataSide == getString(context, R.string.left_advertising_data)) {
        advertisingSide = HearingAidModel.Side.Left
    }
    if (manDataSide == getString(context, R.string.right_advertising_data)) {
        advertisingSide = HearingAidModel.Side.Right
    }
    return BLEDeviceWrapper(
        deviceMAC.getString(getString(context, R.string.device_id_json)),
        deviceName.getString(getString(context, R.string.device_name_json)),
        deviceRSSI.getInt(getString(context, R.string.device_rssi_json)),
        manufacturingData.getString(getString(context, R.string.device_manu_data_json)),
        advertisingSide
    )
}