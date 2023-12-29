package com.alcanl.cleverear.sdk.events;

import androidx.annotation.NonNull;

public class AuxChangeEvent extends DeviceSpecificEvent {
    public final byte auxLevel;
    public AuxChangeEvent(String address, byte auxLevel) {
        super(address);
        this.auxLevel = auxLevel;
    }
    @NonNull
    @Override
    public String toString() {
        return super.toString() + ", AuxChangeEvent{" +
                "auxLevel=" + auxLevel +
                '}';
    }
}
