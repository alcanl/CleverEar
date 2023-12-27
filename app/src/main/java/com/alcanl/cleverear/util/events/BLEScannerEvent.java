package com.alcanl.cleverear.util.events;

public abstract class BLEScannerEvent {

    public final String data;

    /**
     * Constructor
     * DeviceSpecificEvent is called to initialize a device specific event
     *
     * @param data the address of the device initiating the event.
     **/
    protected BLEScannerEvent(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BluetoothAdapterEvent{" +
                "data='" + data + '\'' +
                '}';
    }
}
