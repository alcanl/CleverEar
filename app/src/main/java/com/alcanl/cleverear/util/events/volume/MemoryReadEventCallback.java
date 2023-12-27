/*
----------------------------------------------------------------------------
Copyright (c) 2021 Semiconductor Components Industries, LLC
(d/b/a onsemi). All Rights Reserved.

This code is the property of onsemi and may not be redistributed
in any form without prior written permission from onsemi. The
terms of use and warranty for this code are covered by contractual
agreements between onsemi and the licensee.
----------------------------------------------------------------------------
VolumeChangedEvent.java
- Volume has changed event for volume feature
----------------------------------------------------------------------------
*/
package com.alcanl.cleverear.util.events.volume;

import com.alcanl.cleverear.util.events.DeviceSpecificEvent;


/**
 * VolumeChangeEvent is used to signify a change in the volume level on the hearing aid
 **/
public class MemoryReadEventCallback extends DeviceSpecificEvent {

    public final byte currentMemory;

    /**
     * VolumeChangeEvent is called to initialize a volume change event
     *
     * @param address       the address of the HA reporting its volume level.
     * @param currentMemory the memory value being reported from the HA device.
     **/
    public MemoryReadEventCallback(String address, byte currentMemory) {
        super(address);
        this.currentMemory = currentMemory;
    }

    @Override
    public String toString() {
        return super.toString() + ", MemoryReadCallBack{" +
                "Memory=" + currentMemory +
                '}';
    }
}
