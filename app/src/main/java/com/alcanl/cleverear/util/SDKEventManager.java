/*
----------------------------------------------------------------------------
Copyright (c) 2021 Semiconductor Components Industries, LLC
(d/b/a onsemi). All Rights Reserved.

This code is the property of onsemi and may not be redistributed
in any form without prior written permission from onsemi. The
terms of use and warranty for this code are covered by contractual
agreements between onsemi and the licensee.
----------------------------------------------------------------------------
GattManager.java
- GATT Manager class 
----------------------------------------------------------------------------
*/

/*
 * Copyright 2015 Nordic Semiconductor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alcanl.cleverear.util;

import android.os.AsyncTask;
import android.util.Log;
import com.alcanl.cleverear.util.events.BLEAdapterEvent;
import com.alcanl.cleverear.util.events.ScanEvent;
import com.alcanl.cleverear.util.events.connection.ConnectionStateChangedEvent;
import com.alcanl.cleverear.util.events.volume.CurrentMemoryCharacteristicChangedEvent;
import com.alcanl.cleverear.util.events.volume.VolumeChangeEvent;
import com.ark.ArkException;
import com.ark.Event;
import com.ark.EventHandler;
import com.ark.EventType;
import com.ark.ProductManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.alcanl.cleverear.util.eventhandlers.EventBus;



/**
 * GattManager class handles queuing/sending/receiving GATT operations, there is only one manager
 * which handles synchronization between the left and right hearing aids.
 **/
public class SDKEventManager {


    private static final String TAG = SDKEventManager.class.getName();
    private static final SDKEventManager singleton = new SDKEventManager();
    private static final int DEVICE_MAC = 0;
    private static final int OBJ_ONE = 0;
    private static final int OBJ_TWO = 1;
    private static EventHandler eventHandler;
    private static ProductManager productManager;


    public static SDKEventManager instance() {
        return singleton;
    }

    public void setProductManager(ProductManager productManager) throws ArkException {
        SDKEventManager.productManager = productManager;
        try {
            eventHandler = SDKEventManager.productManager.getEventHandler();
        } catch (ArkException e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {

                    while (true) {
                        Event event = eventHandler.getEvent();
                        EventType type = event.getType();
                        Log.i(TAG, event.getType().name());
                        Log.i(TAG, event.getData());

                        JSONObject jsonObject = new JSONObject(event.getData());
                        JSONArray jsonEvent = jsonObject.getJSONArray("Event");
                        JSONObject deviceMAC = jsonEvent.getJSONObject(DEVICE_MAC);
                        switch (type) {

                            case kActiveEvent:
                                JSONObject bleAdaptorEventJSONObject = jsonEvent.getJSONObject(OBJ_ONE);
                                EventBus.postEvent(BLEAdapterEvent.class.getName(), new BLEAdapterEvent(bleAdaptorEventJSONObject.getInt("IsActive")));
                                //Log.i(TAG, type.name());
                                //Log.i(TAG, event.getData());
                                break;
                            case kScanEvent:
                                //Log.i(TAG, type.name());
                                //Log.i(TAG, event.getData());
                                EventBus.postEvent(ScanEvent.class.getSimpleName(), new ScanEvent(event.getData()));
                                break;
                            case kConnectionEvent:
                                JSONObject deviceConnStat = jsonEvent.getJSONObject(OBJ_TWO);
                                EventBus.postEvent(ConnectionStateChangedEvent.class.getName(), new ConnectionStateChangedEvent(deviceMAC.getString("DeviceID"), deviceConnStat.getInt("ConnectionState")));
                                //Log.i(TAG, type.name());
                                //Log.i(TAG, event.getData());
                                break;
                            case kBatteryEvent:
                                JSONObject deviceBatteryLevel = jsonEvent.getJSONObject(OBJ_TWO);
//                                EventBus.postEvent(BatteryStateChangedEvent.class.getName(), new BatteryStateChangedEvent(deviceMAC.getString("DeviceID"), deviceBatteryLevel.getInt("BatteryLevel")));
                                //Log.i(TAG, type.name());
                                //Log.i(TAG, event.getData());
                                break;
                            case kVolumeEvent:
                                //Log.i(TAG, type.name());
                                //Log.i(TAG, event.getData());
                                JSONObject volume = jsonEvent.getJSONObject(OBJ_TWO);
                                EventBus.postEvent(VolumeChangeEvent.class.getName(), new VolumeChangeEvent(deviceMAC.getString("DeviceID"), (byte) volume.getInt("VolumeLevel")));
                                break;
                            case kMemoryEvent:
                                //Log.i(TAG, type.name());
                                //Log.i(TAG, event.getData());
                                JSONObject memory = jsonEvent.getJSONObject(OBJ_TWO);
                                EventBus.postEvent(CurrentMemoryCharacteristicChangedEvent.class.getName(), new CurrentMemoryCharacteristicChangedEvent(deviceMAC.getString("DeviceID"), (byte) memory.getInt("CurrentMemory")));
                                break;
                        }

                    }
                } catch (ArkException e) {
                    Log.e(TAG, e.getMessage());
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        };
        AsyncTask.execute(runnable);
    }
}