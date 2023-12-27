/*
----------------------------------------------------------------------------
Copyright (c) 2021 Semiconductor Components Industries, LLC
(d/b/a onsemi). All Rights Reserved.

This code is the property of onsemi and may not be redistributed
in any form without prior written permission from onsemi. The
terms of use and warranty for this code are covered by contractual
agreements between onsemi and the licensee.
----------------------------------------------------------------------------
DeviceSpecificEvent.java
- A device specific Event for device related features
----------------------------------------------------------------------------
*/
package com.alcanl.cleverear.util.events;

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
