package com.alcanl.cleverear.sdk.events;

import android.util.Log;


public class ScanEvent extends BLEScannerEvent {
    private static final String TAG = ScanEvent.class.getSimpleName();

    public ScanEvent(String data) {
        super(data);
        Log.i(TAG, data);
    }

    @Override
    public String toString() {
        return super.toString() + "ScanEvent{" + data + "}";
    }
}
