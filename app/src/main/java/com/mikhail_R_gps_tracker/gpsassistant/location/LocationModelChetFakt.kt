package com.mikhail_R_gps_tracker.gpsassistant.location

import java.io.Serializable

data class LocationModelChetFakt(
    val speedChet: Float = 0.0f,
    val kmChetFakt: Float = 0.0f,
    val kmDistanceChetFakt: Int = 0,
    val pkDistanceChetFakt: Int = 0,
    val tvOgrChet15: String = "",
    val tvOgrChet25: String = "",
    val tvOgrChet40: String = "",
    val tvOgrChet50: String = "",
    val tvOgrChet55: String = "",
    val tvOgrChet60: String = "",
    val tvOgrChet65: String = "",
    val tvOgrChet70: String = "",
    val tvOgrChet75: String = "",
    val tvKmPkChet15: String = "",
    val tvKmPkChet25: String = "",
    val tvKmPkChet40: String = "",
    val tvKmPkChet50: String = "",
    val tvKmPkChet55: String = "",
    val tvKmPkChet60: String = "",
    val tvKmPkChet65: String = "",
    val tvKmPkChet70: String = "",
    val tvKmPkChet75: String = ""
) : Serializable
