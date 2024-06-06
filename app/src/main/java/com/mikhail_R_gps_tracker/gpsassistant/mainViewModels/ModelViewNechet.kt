package com.mikhail_R_gps_tracker.gpsassistant.mainViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.MainDb
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.MainDbNechet
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.TrackItem
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.TrackItemNechet
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationModelNechet
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationModelNechetFakt
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ModelViewNechet(db: MainDbNechet) : ViewModel() {
    private val dao = db.getDaoNechet()
    val locationUpdatesNechet = MutableLiveData<LocationModelNechet>()
    val locationUpdatesNechetFakt = MutableLiveData<LocationModelNechetFakt>()
    val currentTrackNechet = MutableLiveData<TrackItemNechet>()
//    val timeDataNechet = MutableLiveData<String>()
    val tracksNechet = dao.getAllTracksNechet().asLiveData()

    suspend fun insertTrackNechet(trackItemNechet: TrackItemNechet) = viewModelScope.launch {
        dao.insertTrackNechet(trackItemNechet)
    }

    fun deleteTrackNechet(trackItemNechet: TrackItemNechet) = viewModelScope.launch {
        dao.deleteTrackNechet(trackItemNechet)
    }

    class ViewModelFactory(private val db: MainDbNechet) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ModelViewNechet::class.java)){
                return ModelViewNechet(db) as T
            }
            throw IllegalArgumentException("Неизвестный класс")
        }
    }
}