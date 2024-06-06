package com.mikhail_R_gps_tracker.gpsassistant.location

import java.io.Serializable

data class LocationModelNechetFakt(
    val speedNechet: Float = 0.0f,
    val kmNechetFakt: Float = 0.0f,
    val kmDistanceNechetFakt: Int = 0,
    val pkDistanceNechetFakt: Int = 0,
    val tvOgrNechet15: String = "",
    val tvOgrNechet25: String = "",
    val tvOgrNechet40: String = "",
    val tvOgrNechet50: String = "",
    val tvOgrNechet55: String = "",
    val tvOgrNechet60: String = "",
    val tvOgrNechet65: String = "",
    val tvOgrNechet70: String = "",
    val tvOgrNechet75: String = "",
    val tvKmPkNechet15: String = "",
    val tvKmPkNechet25: String = "",
    val tvKmPkNechet40: String = "",
    val tvKmPkNechet50: String = "",
    val tvKmPkNechet55: String = "",
    val tvKmPkNechet60: String = "",
    val tvKmPkNechet65: String = "",
    val tvKmPkNechet70: String = "",
    val tvKmPkNechet75: String = ""
) : Serializable
