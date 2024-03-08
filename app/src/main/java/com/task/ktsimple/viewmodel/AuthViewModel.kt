package com.task.ktsimple.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.task.ktsimple.database.RealmDB
import com.task.ktsimple.enums.AuthErrors
import com.task.ktsimple.enums.AuthState
import com.task.ktsimple.exceptions.AuthException
import com.task.ktsimple.model.User
import kotlin.jvm.Throws


/**
 * Created by Admin on 08,March,2024
 */
class AuthViewModel : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    private val _username = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()

    private val _currentUser = MutableLiveData<User>()
    private val _signInUserList = MutableLiveData<MutableList<User>>()

    // Expose LiveData to observe changes in the UI
    val authState: LiveData<AuthState> get() = _authState
    val userName: LiveData<String> get() = _username
    val passWord: LiveData<String> get() = _password

    val currentUser: LiveData<User>
        get() = _currentUser
    val signedInUsers: LiveData<MutableList<User>>
        get() = _signInUserList

    init {
        updateUserName("")
        updatePassword("")
        updateSignedInUsers(RealmDB.getAllSignedInUser())
    }

    fun updateUserName(username: String) {
        _username.value = username
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun updateAuthState(state: AuthState) {
        _authState.value = state
    }

    fun updateSignedInUsers(users: List<User>) {
        if (_signInUserList.value == null) _signInUserList.value = mutableListOf()
        _signInUserList.value?.clear()
        _signInUserList.value?.addAll(users)
    }

    fun addSignedInUser(user: User) {

        // dont add to the list if user is already signed in
        for (i in _signInUserList.value!!.indices) {
            if (user.userName == _signInUserList.value!![i].userName) return
        }

        _signInUserList.value?.add(user)
        _signInUserList.value = _signInUserList.value
    }

    fun updateCurrentUser(user : User) {
        _currentUser.value = user
    }

    @Throws(AuthException::class)
    fun authUser() : User{

        if (userName.value!!.length < 4) throw AuthException(AuthErrors.USERNAME_TOO_SMALL)
        if (passWord.value!!.length < 4) throw AuthException(AuthErrors.PASSWORD_TOO_SMALL)

        val user = User(userName.value!!, passWord.value!!)

        return if (authState.value == AuthState.SIGN_IN) {
            val signedInUser = RealmDB.loginUser(user)
            updateCurrentUser(user)
            addSignedInUser(signedInUser)
            signedInUser
        } else {
            val signedInUser = RealmDB.createUser(user)
            updateCurrentUser(user)
            addSignedInUser(signedInUser)
            signedInUser
        }
    }
}