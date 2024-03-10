package com.task.ktsimple.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.task.ktsimple.database.RealmDB
import com.task.ktsimple.model.Location
import com.task.ktsimple.model.User

/**
 * Created by Admin on 10,March,2024
 */
class MapsActivityViewModel : ViewModel() {

    private val _currentUser = MutableLiveData<User>()
    private val  _playingLocation = MutableLiveData<Boolean>()
    private val _animatingIndex = MutableLiveData<Int>()
    private val _animatingLocation = MutableLiveData<Location>()

    val playingLocation : LiveData<Boolean> get() = _playingLocation
    val animatingLocation : LiveData<Location> get() = _animatingLocation
    val animatingIndex : LiveData<Int> get() = _animatingIndex

    var totalLocation : Int = 0

    init {
        _currentUser.value = RealmDB.currentUser
        _playingLocation.value = false
        totalLocation = _currentUser.value!!.locations.size

        if (totalLocation > 0) {
            _animatingIndex.value = 0
            _animatingLocation.value = _currentUser.value!!.locations[0]
        }
    }

    fun playOrPause() {
        _playingLocation.value = !playingLocation.value!!
    }

    fun getInitialLocation() : Location? {
        if (totalLocation > 0) {
            return _currentUser.value!!.locations[0]
        }
        return null
    }

    fun incrementAnimatingIndex() {
        _animatingIndex.value = _animatingIndex.value!! + 1
        _animatingLocation.value = _currentUser.value!!.locations[_animatingIndex.value!!]
    }

}