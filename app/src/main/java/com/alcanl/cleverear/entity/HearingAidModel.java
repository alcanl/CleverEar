/*
---------------------------------------------------------------------
HearingAidModel.java
- Hearing Aid Model used to support individual hearing aids
----------------------------------------------------------------------------
*/
package com.alcanl.cleverear.entity;

import android.util.Log;
import com.alcanl.cleverear.helpers.Configuration;
import com.alcanl.cleverear.sdk.events.AuxChangeEvent;
import com.alcanl.cleverear.sdk.events.BatteryStateChangedEvent;
import com.alcanl.cleverear.sdk.events.ConnectionStateChangedEvent;
import com.alcanl.cleverear.sdk.events.CurrentMemoryCharacteristicChangedEvent;
import com.alcanl.cleverear.sdk.events.MicChangeEvent;
import com.alcanl.cleverear.sdk.events.VolumeChangeEvent;
import com.alcanl.cleverear.sdk.events.handlers.EventBus;
import com.alcanl.cleverear.sdk.events.handlers.EventReceiver;
import com.ark.ArkException;
import com.ark.CommunicationAdaptor;
import com.ark.ParameterSpace;
import com.ark.WirelessCommunicationAdaptorStateType;
import com.ark.WirelessControl;
import java.util.HashMap;
/**
 * Data object defining the model of the data associated with a hearing aid.
 */
public class HearingAidModel {

    private static final String TAG = HearingAidModel.class.getName();
    static private int instanceCount = 0;

    public final Configuration configuration = new Configuration();
    public CommunicationAdaptor communicationAdaptor;
    public WirelessControl wirelessControl;
    public boolean autoConnect = true;
    public boolean isConfigured = false;
    public HashMap<String, ParameterSpace> MemoriesMap = new HashMap<>();
    public boolean connected;
    public int numberOfMemories;
    public int connectionStatus = 1;
    /**
     * The name of the associated hearing aid
     */
    public String name;
    /**
     * The MAC address of the associated hearing aid
     */

    public String address;
    /**
     * The user defined friendly name of the hearing aid
     */
    public String friendlyName;
    /**
     * The volume of the hearing aid, we expect this to be 5 by default
     */
    public int volume = 0;
    public int AuxAttenuation = 0;
    public int MicAttenuation = 8;
    public int StreamAddress = 16;
    public int StreamAddressRaw = 1101286;
    public int StreamChannelList = 1;
    /**
     * The current memory of the hearing aid, we expect this to be 0 by default
     */
    public int currentMemory = 0;
    /**
     * The last reported level of the battery for the hearing aid.
     */
    public int batteryLevel = -1;
    /**
     * variable that stores the manufacturer specific data information
     */
    public String manufacturerSpecificData;
    /**
     * Remember which side this particular hearing aid model is expecting.
     */
    public Side side;

    private boolean cleanUp = false;

    private final EventReceiver<ConnectionStateChangedEvent> ConnectionStateChangedHandler
            = new EventReceiver<>() {
        @Override
        public void onEvent(String name, ConnectionStateChangedEvent data) {
            if (data.address.equals(address)) {
                connectionStatus = data.connectionStatus;
                if (WirelessCommunicationAdaptorStateType.kDisconnected.ordinal() == data.connectionStatus) {
                    whenDisconnected();
                } else if (WirelessCommunicationAdaptorStateType.kConnected.ordinal() == data.connectionStatus) {
                    whenConnected();
                }
                issueConfigurationChangedEvent();
            }
        }
    };
    private final EventReceiver<BatteryStateChangedEvent> BatteryStateChangedHandler
            = new EventReceiver<>() {
        @Override
        public void onEvent(String name, BatteryStateChangedEvent data) {
            if (data.address.equals(address)) {
                if (data.level != batteryLevel) {
                    batteryLevel = data.level;
                    issueConfigurationChangedEvent();
                }
            }

        }
    };
    private final EventReceiver<CurrentMemoryCharacteristicChangedEvent> currentMemoryChangedEventHandler = new EventReceiver<>() {

        @Override
        public void onEvent(String name, CurrentMemoryCharacteristicChangedEvent data) {
            if (data.address.equals(address)) {
                if (data.currentMemory != currentMemory) {
                    currentMemory = data.currentMemory;
                    issueConfigurationChangedEvent();
                }
            }
        }
    };
    private final EventReceiver<VolumeChangeEvent> volumeChangeEvent = new EventReceiver<>() {
        @Override
        public void onEvent(String name, VolumeChangeEvent data) {

            if (data.address.equals(address)) {
                if (volume != data.volume) {
                    Log.i(TAG, "EV volume of side " + side + "is " + data.volume);
                    volume = data.volume;
                    issueConfigurationChangedEvent();
                }
            }
        }
    };
    private final EventReceiver<MicChangeEvent> micChangeEvent = new EventReceiver<>() {
        @Override
        public void onEvent(String name, MicChangeEvent data) {

            if (data.address.equals(address)) {
                if (MicAttenuation != data.miclevel) {
                    Log.i(TAG, "EV miclevel of side " + side + "is " + data.miclevel);
                    MicAttenuation = data.miclevel;
                    issueConfigurationChangedEvent();
                }
            }
        }
    };    private final EventReceiver<AuxChangeEvent> auxChangeEvent = new EventReceiver<>() {
        @Override
        public void onEvent(String name, AuxChangeEvent data) {

            if (data.address.equals(address)) {
                if (AuxAttenuation != data.auxLevel) {
                    Log.i(TAG, "EV auxlevel of side " + side + "is " + data.auxLevel);
                    AuxAttenuation = data.auxLevel;
                    issueConfigurationChangedEvent();
                }
            }
        }
    };
    /**
     * Constructor for a hearing aid.
     * This method saves the side locally that it represents and registers to receive all events that
     * pertain to it (such as volume level change etc.)
     *
     * @param side The side on which the hearing aid will be worn, this is immutable
     */
    public HearingAidModel(Side side, String address) {
        this.side = side;
        this.address = address;
        instanceCount++;
        Log.i(TAG, "instance count: " + instanceCount);
        setWirelessControlAndMemoryValues();
        registerReceiver();
    }

    private void whenDisconnected() {
        connected = false;
        isConfigured = false;
        if (autoConnect) {
            try {
                Log.e(TAG, "Attempting Reconnect");
                android.os.SystemClock.sleep(1000);
                communicationAdaptor.connect();
            } catch (ArkException e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            if (cleanUp) {
                try {
                    android.os.SystemClock.sleep(1000);
                    communicationAdaptor.closeDevice();
                    android.os.SystemClock.sleep(1000);
                } catch (ArkException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }
                unregisterReceivers();
            }

        }
    }

    private void whenConnected() {
        connected = true;
        //      BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //      Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        //      for (BluetoothDevice bt : pairedDevices) {
        //          String btaddr = bt.getAddress();
        //          if (btaddr.equals(address)) isdevpaired = true;
        //       }
        //      if (isdevpaired) {
        try {
            batteryLevel = wirelessControl.getBatteryLevel();
            volume = wirelessControl.getVolume();
            currentMemory = (wirelessControl.getCurrentMemory().ordinal()) - 4;
            MicAttenuation = wirelessControl.getMicAttenuation();
            AuxAttenuation = wirelessControl.getAuxAttenuation();
            StreamChannelList = wirelessControl.getStreamChannelList();
            StreamAddressRaw = wirelessControl.getStreamAddress();
            StreamAddress = StreamAddressRaw >> 16;
        } catch (ArkException e) {
            Log.e(TAG, e.getMessage());
        }
        setNumberOfMemories();
        //    }
    }

    private void setNumberOfMemories() {
        try {
            numberOfMemories = wirelessControl.getNumberOfMemories();
            // Memory HashMap for view
            for (int i = 0; i < numberOfMemories; i++) {
                MemoriesMap.put("Memory " + (i + 1), ParameterSpace.values()[i + 4]);
            }
        } catch (ArkException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    private void setWirelessControlAndMemoryValues() {
        try {
            communicationAdaptor = configuration.productManager.createWirelessCommunicationInterface(address);
            communicationAdaptor.setEventHandler(configuration.productManager.getEventHandler());
            wirelessControl = configuration.productManager.getWirelessControl();
            wirelessControl.setCommunicationAdaptor(communicationAdaptor);
        } catch (ArkException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Getter for the hearing aid side.
     *
     * @return The side as an enum value Left or Right
     */
    public Side getSide() {
        return side;
    }

    private void issueConfigurationChangedEvent() {
        configuration.issueConfigurationChangedEvent(address);
    }


    /**
     * Method to register this hearing aid for all events it would be interested in such as the
     * volume level change event etc.
     */
    void registerReceiver() {
        EventBus.registerReceiver(currentMemoryChangedEventHandler, CurrentMemoryCharacteristicChangedEvent.class.getName());
        EventBus.registerReceiver(BatteryStateChangedHandler, BatteryStateChangedEvent.class.getName());
        EventBus.registerReceiver(volumeChangeEvent, VolumeChangeEvent.class.getName());
        EventBus.registerReceiver(auxChangeEvent, AuxChangeEvent.class.getName());
        EventBus.registerReceiver(micChangeEvent, MicChangeEvent.class.getName());
        EventBus.registerReceiver(ConnectionStateChangedHandler, ConnectionStateChangedEvent.class.getName());

    }

    /**
     * Method to register this hearing aid for all events it would be interested in such as the
     * volume level change event etc.
     */
    public void unregisterReceivers() {

        EventBus.unregisterReceiver(BatteryStateChangedHandler);
        EventBus.unregisterReceiver(ConnectionStateChangedHandler);
        EventBus.unregisterReceiver(volumeChangeEvent);
        EventBus.unregisterReceiver(auxChangeEvent);
        EventBus.unregisterReceiver(micChangeEvent);
        EventBus.unregisterReceiver(currentMemoryChangedEventHandler);
    }

    public void cleanup() {
        cleanUp = true;
    }

    /**
     * Hearing aids can be worn on the left or right so define an enum to capture this.
     */
    public enum Side {
        Left, Right
    }

}