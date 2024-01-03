/*

----------------------------------------------------------------------------
EventBus.java
- EventBus related functions
----------------------------------------------------------------------------
*/

package com.alcanl.cleverear.dto;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.alcanl.cleverear.entity.HearingAidModel;

/**
 * BLEDeviceWrapper class creates a wrapper around a BLE device.
 */
public class BLEDeviceWrapper {
    private final String address;
    private final String name;
    private final int rssi;
    private final HearingAidModel.Side side;
    String manufacturerData;
    private int bondState;

    /**
     * Constructor
     * BLEDeviceWrapper called to initialize a wrapper around a BLE device
     * //     * @param device the BLE device which represents the HA.
     *
     * @param rssi the current RSSI value for this BLE device
     *             //     * @param side the hearing aid side this device represents (left, right)
     */
    public BLEDeviceWrapper(String address, String name, int rssi, String manufacturerData, HearingAidModel.Side side) {
        this.address = address;
        this.side = side;
        this.name = name;
        this.rssi = rssi;
        this.manufacturerData = manufacturerData;
    }
    public String getAddress() {
        return address;
    }
    public String getName() {
        return name;
    }
    public int getRssi() {
        return rssi;
    }
    public String getManufacturerData() {
        return manufacturerData;
    }
    public void setManufacturerData(String manufacturerData) {
        this.manufacturerData = manufacturerData;
    }
    public HearingAidModel.Side getSide() {
        return side;
    }
    @NonNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString()
    {
        return String.format("%s %s %d %s", address, name, rssi, manufacturerData);
    }

}
