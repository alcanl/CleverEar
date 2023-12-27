/*
----------------------------------------------------------------------------
Copyright (c) 2021 Semiconductor Components Industries, LLC
(d/b/a onsemi). All Rights Reserved.

This code is the property of onsemi and may not be redistributed
in any form without prior written permission from onsemi. The
terms of use and warranty for this code are covered by contractual
agreements between onsemi and the licensee.
----------------------------------------------------------------------------
EventReceiver.java
- Event Receiver Handlers 
----------------------------------------------------------------------------
*/

/* ---------------------------------------------------------------------------
 * Original Copyright Notice
 *
 * Copyright 2015 Alex Yanchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ----------------------------------------------------------------------------
 */

package com.alcanl.cleverear.util.eventhandlers;

import org.json.JSONException;

/**
 * EventReceiver is an interface to the event receiver which triggers an onEvent operation when
 * the receiver receives notice that an event has occurred.
 **/
public interface EventReceiver<T> {
    void onEvent(String name, T data) throws JSONException;
}