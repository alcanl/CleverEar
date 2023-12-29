/*

----------------------------------------------------------------------------
EventBusReceiverList.java
- EventBus Receiver list
----------------------------------------------------------------------------
*/
package com.alcanl.cleverear.sdk.events.handlers;

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
