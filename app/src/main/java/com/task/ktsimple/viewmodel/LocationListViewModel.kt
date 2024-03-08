package com.task.ktsimple.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.task.ktsimple.database.RealmDB
import com.task.ktsimple.model.User

/**
 * Created by Admin on 08,March,2024
 */
class LocationListViewModel : ViewModel() {

    private val _currentUser = MutableLiveData<User>()
    private val _signedInUserList = MutableLiveData<MutableList<User>>()

    val currentUser : LiveData<User> get() = _currentUser
    val signedInUserList : MutableLiveData<MutableList<User>> get() = _signedInUserList

    init {

        _signedInUserList.value = mutableListOf()
        updateSignedInUsersList(RealmDB.getAllSignedInUser())
        Log.d("Iniss" , "obj")
    }

    fun updateCurrentUser(user : User) {
        _currentUser.value = user
    }

    fun updateSignedInUsersList(users: List<User>) {
        if (_signedInUserList.value == null) _signedInUserList.value = mutableListOf()
        _signedInUserList.value?.clear()
        _signedInUserList.value?.addAll(users)
    }

}