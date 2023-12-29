
package com.alcanl.cleverear.sdk.events;

import android.os.AsyncTask;
import android.util.Log;
import com.ark.ArkException;
import com.ark.Event;
import com.ark.EventHandler;
import com.ark.EventType;
import com.ark.ProductManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.alcanl.cleverear.sdk.events.handlers.EventBus;

public class EventManager {
    private static final String TAG = EventManager.class.getName();
    private static final EventManager singleton = new EventManager();
    private static final int DEVICE_MAC = 0;
    private static final int OBJ_ONE = 0;
    private static final int OBJ_TWO = 1;
    private static EventHandler eventHandler;
    public static EventManager instance() {
        return singleton;
    }
    public void setEventHandler(ProductManager productManager) throws ArkException {

        try {
            eventHandler = productManager.getEventHandler();
        } catch (ArkException e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        Runnable runnable = () -> {
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
                        case kScanEvent ->
                                EventBus.postEvent(ScanEvent.class.getSimpleName(), new ScanEvent(event.getData()));
                        case kConnectionEvent -> {
                            JSONObject deviceConnStat = jsonEvent.getJSONObject(OBJ_TWO);
                            EventBus.postEvent(ConnectionStateChangedEvent.class.getName(), new ConnectionStateChangedEvent(deviceMAC.getString("DeviceID"), deviceConnStat.getInt("ConnectionState")));
                        }
                        case kBatteryEvent -> {
                            JSONObject deviceBatteryLevel = jsonEvent.getJSONObject(OBJ_TWO);
                            EventBus.postEvent(BatteryStateChangedEvent.class.getName(), new BatteryStateChangedEvent(deviceMAC.getString("DeviceID"), deviceBatteryLevel.getInt("BatteryLevel")));
                        }
                        case kVolumeEvent -> {
                            JSONObject volume = jsonEvent.getJSONObject(OBJ_TWO);
                            EventBus.postEvent(VolumeChangeEvent.class.getName(), new VolumeChangeEvent(deviceMAC.getString("DeviceID"), (byte) volume.getInt("VolumeLevel")));
                        }
                        case kAuxAttenuationEvent -> {
                            JSONObject auxLevel = jsonEvent.getJSONObject(OBJ_TWO);
                            EventBus.postEvent(AuxChangeEvent.class.getName(), new AuxChangeEvent(deviceMAC.getString("DeviceID"), (byte) auxLevel.getInt("AuxLevel")));
                        }
                        case kMicAttenuationEvent -> {
                            JSONObject micLevel = jsonEvent.getJSONObject(OBJ_TWO);
                            EventBus.postEvent(MicChangeEvent.class.getName(), new MicChangeEvent(deviceMAC.getString("DeviceID"), (byte) micLevel.getInt("MicLevel")));
                        }
                        case kMemoryEvent -> {
                            JSONObject memory = jsonEvent.getJSONObject(OBJ_TWO);
                            EventBus.postEvent(CurrentMemoryCharacteristicChangedEvent.class.getName(), new CurrentMemoryCharacteristicChangedEvent(deviceMAC.getString("DeviceID"), (byte) memory.getInt("CurrentMemory")));
                        }
                    }

                }
            } catch (ArkException e) {
                Log.e(TAG, e.getMessage());
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        };
        AsyncTask.execute(runnable);
    }
}