package com.mikhail_R_gps_tracker.gpsassistant.location

import org.osmdroid.util.GeoPoint
import java.io.Serializable

data class LocationModelNechet(
    val speedNechet: Float = 0.0f,
    val kmNechetFakt: Float = 0.0f,
    val sbL: StringBuilder,
    val distanceNechet: Float = 0.0f,
    val distanceNechet2: Float = 0.0f,
    val geoPointsList: ArrayList<GeoPoint>,
    val kmDistanceNechet: Int = 0,
    val pkDistanceNechet: Int = 0
) : Serializable
