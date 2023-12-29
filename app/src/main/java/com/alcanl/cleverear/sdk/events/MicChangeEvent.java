/*

----------------------------------------------------------------------------
VolumeChangedEvent.java
- Volume has changed event for volume feature
----------------------------------------------------------------------------
*/
package com.alcanl.cleverear.sdk.events;

/**
 * MicChangeEvent is used to signify a change in the mic level on the hearing aid
 **/
public class MicChangeEvent extends DeviceSpecificEvent {

    public final byte miclevel;

    /**
     * MicChangeEvent is called to initialize a mic change event
     *
     * @param address the address of the HA reporting its volume level.
     * @param miclevel  the mic level being reported from the HA device.
     **/
    public MicChangeEvent(String address, byte miclevel) {
        super(address);
        this.miclevel = miclevel;
    }

    @Override
    public String toString() {
        return super.toString() + ", MicChangeEvent{" +
                "miclevel=" + miclevel +
                '}';
    }
}
