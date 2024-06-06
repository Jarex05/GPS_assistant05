package com.mikhail_R_gps_tracker.gpsassistant.dbRoom

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoNechet {
    @Insert
    suspend fun insertTrackNechet(trackItemNechet: TrackItemNechet)
    @Query("SELECT * FROM TRACKNECHET")
    fun getAllTracksNechet(): Flow<List<TrackItemNechet>>
    @Delete
    suspend fun deleteTrackNechet(trackItemNechet: TrackItemNechet)
}