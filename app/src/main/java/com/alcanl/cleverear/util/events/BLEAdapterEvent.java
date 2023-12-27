package com.alcanl.cleverear.util.events;

public class BLEAdapterEvent {
    public boolean isActive;

    public BLEAdapterEvent(int isActive) {
        this.isActive = (isActive == 1);
    }

    @Override
    public String toString() {
        return " BLEAdapterEvent {" + isActive + "}";
    }
}
