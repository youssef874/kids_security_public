package com.example.kidssecurity.view_model.child_home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidssecurity.model.retrofit.RetrofitDao
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class ChildHomeViewModel(private val retrofitRepository: RetrofitDao) : ViewModel() {

    private var _currentLocation = MutableLiveData<LatLng?>()
    val currentLocation: LiveData<LatLng?>
        get() = _currentLocation

    fun updateCurrentLocation(location: LatLng){
        _currentLocation.value = location
    }

    fun postCurrentLocation(lat: Long,log: Long,idChild: Long){
        viewModelScope.launch {
            try {
                retrofitRepository.getLocationService().addLocation(lat, log, idChild)
            }catch (e: Exception){
                Log.e(TAG,"${e.message}")
            }
        }
    }

    companion object{
        const val TAG = "ChildHomeViewModel"
    }
}