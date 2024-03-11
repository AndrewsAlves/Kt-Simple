package com.task.ktsimple.viewmodel

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.task.ktsimple.database.RealmDB
import com.task.ktsimple.model.User
import com.task.ktsimple.services.LocationService

/**
 * Created by Admin on 08,March,2024
 */
class LocationListViewModel : ViewModel() {

    private val _currentUser = MutableLiveData<User>()
    private val _signedInUserList = MutableLiveData<MutableList<User>>()
    private val _locationPermission = MutableLiveData<Boolean>()

    val currentUser : LiveData<User> get() = _currentUser
    val signedInUserList : LiveData<MutableList<User>> get() = _signedInUserList
    val locationPermission : LiveData<Boolean> get() = _locationPermission

    init {
        _currentUser.value = RealmDB.currentUser
        _signedInUserList.value = mutableListOf()
        updateSignedInUsersList(RealmDB.getAllSignedInUser())
        Log.d("Iniss" , "obj")
    }

    fun updateCurrentUser(user : User) {
        _currentUser.value = user
        RealmDB.currentUser =  user
    }

    fun updateUserLocations() {
        _currentUser.value = RealmDB.updateCurrentUserLocations()
    }

    fun updateLocationPermission(b : Boolean) {
        _locationPermission.value = b
    }

    fun updateSignedInUsersList(users: List<User>) {
        if (_signedInUserList.value == null) _signedInUserList.value = mutableListOf()
        _signedInUserList.value?.clear()
        _signedInUserList.value?.addAll(users)
    }

    fun getUserIndex(user : User) : Int {
        for (i in _signedInUserList.value!!.indices) {
            if (user.userName == _signedInUserList.value!![i].userName) return i
        }
        return -1
    }

    fun startLocationService(context : Context) {
        Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            context.startService(this)
        }
    }

}