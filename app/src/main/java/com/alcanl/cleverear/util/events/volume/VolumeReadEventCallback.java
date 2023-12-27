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
public class VolumeReadEventCallback extends DeviceSpecificEvent {

    public final byte volume;

    /**
     * VolumeChangeEvent is called to initialize a volume change event
     *
     * @param address the address of the HA reporting its volume level.
     * @param volume  the volume level being reported from the HA device.
     **/
    public VolumeReadEventCallback(String address, byte volume) {
        super(address);
        this.volume = volume;
    }

    @Override
    public String toString() {
        return super.toString() + ", VolumeReadCallback{" +
                "volume=" + volume +
                '}';
    }
}