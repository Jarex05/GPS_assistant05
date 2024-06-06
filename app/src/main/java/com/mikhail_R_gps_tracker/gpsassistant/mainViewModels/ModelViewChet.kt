package com.mikhail_R_gps_tracker.gpsassistant.mainViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.MainDb
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.TrackItem
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationModelChet
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationModelChetFakt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
@Suppress("UNCHECKED_CAST")
class ModelViewChet(db: MainDb) : ViewModel() {
    private val dao = db.getDao()
    val locationUpdatesChet = MutableLiveData<LocationModelChet>()
    val locationUpdatesChetFakt = MutableLiveData<LocationModelChetFakt>()
    val currentTrack = MutableLiveData<TrackItem>()
//    val timeDataChet = MutableLiveData<String>()
    val tracks = dao.getAllTracks().asLiveData()

    suspend fun insertTrack(trackItem: TrackItem) = viewModelScope.launch {
        dao.insertTrack(trackItem)
    }

    fun deleteTrack(trackItem: TrackItem) = viewModelScope.launch {
        dao.deleteTrack(trackItem)
    }

    class ViewModelFactory(private val db: MainDb) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ModelViewChet::class.java)){
                return ModelViewChet(db) as T
            }
            throw IllegalArgumentException("Неизвестный класс")
        }
    }
}