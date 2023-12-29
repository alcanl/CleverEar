/*

----------------------------------------------------------------------------
DeviceSpecificEvent.java
- A device specific Event for device related features
----------------------------------------------------------------------------
*/
package com.alcanl.cleverear.sdk.events;

/**
 * DeviceSpecificEvent is used to signify a change in a device has occurred.
 **/
public abstract class DeviceSpecificEvent {

    public final String address;

    /**
     * Constructor
     * DeviceSpecificEvent is called to initialize a device specific event
     *
     * @param address the address of the device initiating the event.
     **/
    protected DeviceSpecificEvent(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "DeviceSpecificEvent{" +
                "address='" + address + '\'' +
                '}';
    }
}
