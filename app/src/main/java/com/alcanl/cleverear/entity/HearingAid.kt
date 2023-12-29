package com.alcanl.cleverear.entity

import com.alcanl.cleverear.helpers.EarSide
import com.ark.CommunicationAdaptor
import com.ark.WirelessControl

data class HearingAid(val id: Int, val name: String, val numberOfMemories: Int, var volume: Int,
                      var batteryLevel : Int, val side: EarSide, var communicationAdaptor: CommunicationAdaptor,
                      var wirelessControl: WirelessControl, var autoConnect : Boolean, ) {
}