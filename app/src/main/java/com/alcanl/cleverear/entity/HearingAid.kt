package com.alcanl.cleverear.entity

import com.alcanl.cleverear.helpers.EarSide
import com.ark.CommunicationAdaptor
import com.ark.ParameterSpace
import com.ark.WirelessControl
import java.io.Serializable

data class HearingAid(val address: String, val name: String, val rssi: Int, val manufacturerData: String,
                      val side: EarSide, var isBond : Boolean = false, var numberOfMemories: Int = 8,
                      var volume: Int = 0, var batteryLevel : Int = 100, var communicationAdaptor: CommunicationAdaptor? = null,
                      var wirelessControl: WirelessControl? = null, var autoConnect : Boolean = false,
                      var currentMemory :  ParameterSpace? = null, var auxAttenuation : Int = 0,
                      var micAttenuation : Int = 8, var streamAddress : Int = 16,
                      var streamAddressRaw : Int = 1101286, var streamChannelList : Int = 1, ) : Serializable

