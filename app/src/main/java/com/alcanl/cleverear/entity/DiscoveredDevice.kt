package com.alcanl.cleverear.entity

import com.alcanl.cleverear.helpers.EarSide

data class DiscoveredDevice(val address: String, val name: String, val rssi: Int, val manufacturerData: String, val isBond: Boolean = false) {

}
