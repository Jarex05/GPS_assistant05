package com.mikhail_R_gps_tracker.gpsassistant.location

import org.osmdroid.util.GeoPoint
import java.io.Serializable

data class LocationModelChet(
    val speedChet: Float = 0.0f,
    val kmChetFakt: Float = 0.0f,
    val sbL: StringBuilder,
    val distanceChet: Float = 0.0f,
    val distanceChet2: Float = 0.0f,
    val geoPointsList: ArrayList<GeoPoint>,
    val kmDistanceChet: Int = 0,
    val pkDistanceChet: Int = 0
) : Serializable
