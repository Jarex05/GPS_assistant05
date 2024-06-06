package com.mikhail_R_gps_tracker.gpsassistant.dbRoom

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "trackNechet")
data class TrackItemNechet(
    @PrimaryKey (autoGenerate = true)
    val id: Int?,
    @ColumnInfo (name = "titleNechet")
    var titleNechet: String,
    @ColumnInfo (name = "distanceNechet")
    val distanceNechet: String,
    @ColumnInfo (name = "geo_pointsNechet")
    val geoPointsNechet: String,
)
