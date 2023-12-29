/*

----------------------------------------------------------------------------
VolumeChangedEvent.java
- Volume has changed event for volume feature
----------------------------------------------------------------------------
*/
package com.alcanl.cleverear.sdk.events;

/**
 * VolumeChangeEvent is used to signify a change in the volume level on the hearing aid
 **/

public class VolumeChangeEvent extends DeviceSpecificEvent {

    public final byte volume;

    /**
     * VolumeChangeEvent is called to initialize a volume change event
     *
     * @param address the address of the HA reporting its volume level.
     * @param volume  the volume level being reported from the HA device.
     **/
    public VolumeChangeEvent(String address, byte volume) {
        super(address);
        this.volume = volume;
    }

    @Override
    public String toString() {
        return super.toString() + ", VolumeChangeEvent{" +
                "volume=" + volume +
                '}';
    }
}
