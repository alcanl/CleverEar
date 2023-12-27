/*
----------------------------------------------------------------------------
Copyright (c) 2021 Semiconductor Components Industries, LLC
(d/b/a onsemi). All Rights Reserved.

This code is the property of onsemi and may not be redistributed
in any form without prior written permission from onsemi. The
terms of use and warranty for this code are covered by contractual
agreements between onsemi and the licensee.
----------------------------------------------------------------------------
EventBusReceiverList.java
- EventBus Receiver list 
----------------------------------------------------------------------------
*/
package com.alcanl.cleverear.util.eventhandlers;

import java.util.concurrent.ConcurrentHashMap;

/**
 * EventBusReceiverList is a list of all receivers for events
 **/
public class EventBusReceiverList extends ConcurrentHashMap<EventReceiver<Object>, Boolean> {

    /**
     * put is called when a receiver is registered this will place the receiver in the receiver list.
     *
     * @param receiver the receiver object to place in the list.
     **/
    public void put(EventReceiver<Object> receiver) {
        put(receiver, Boolean.FALSE);
    }

}
