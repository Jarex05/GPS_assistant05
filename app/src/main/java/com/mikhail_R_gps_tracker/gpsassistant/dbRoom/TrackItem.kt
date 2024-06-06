package com.mikhail_R_gps_tracker.gpsassistant.dbRoom

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "track")
data class TrackItem(
    @PrimaryKey (autoGenerate = true)
    val id: Int?,
    @ColumnInfo (name = "title")
    var title: String,
    @ColumnInfo (name = "distance")
    val distance: String,
    @ColumnInfo (name = "geo_points")
    val geoPoints: String,
)
