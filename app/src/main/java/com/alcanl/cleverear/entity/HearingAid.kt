package com.alcanl.cleverear.entity

import com.alcanl.cleverear.entity.helpers.EarSide

data class HearingAid(val id: Int, val name: String, val numberOfMemories: Int, var volume: Int,
                      var batteryLevel : Int, val side: EarSide, ) {
}