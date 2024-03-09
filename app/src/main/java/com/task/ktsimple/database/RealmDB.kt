package com.task.ktsimple.database

import android.util.Log
import com.task.ktsimple.enums.AuthErrors
import com.task.ktsimple.exceptions.AuthException
import com.task.ktsimple.model.Location
import com.task.ktsimple.model.User
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlin.jvm.Throws

/**
 * Created by Admin on 08,March,2024
 */

object RealmDB {

    var realmDb : Realm? = null

    var currentUser : User? = null

    val TAG = "Realm DB Kt"

    private fun openRealm() {
        val config = RealmConfiguration.create(schema = setOf(User::class, Location::class))
        realmDb = Realm.open(config)
    }

    private fun close() {
        realmDb?.close()
        realmDb = null
    }

    @Throws(AuthException::class)
    fun loginUser(user: User) : User {

        if (realmDb == null) openRealm()
        // Check if user exists
        try {
            val userResult: User = realmDb!!.query<User>("userName == $0", user.userName).find().first()

            if (userResult.signedIn) return userResult
            if (userResult.passWord != user.passWord) {
                throw AuthException(AuthErrors.PASSWORD_MISMATCH)
            }

        } catch (e: NoSuchElementException) {
            throw AuthException(AuthErrors.USERNAME_NOT_FOUND)
        }

        // update that user is signer in
        realmDb!!.writeBlocking {
            val userResult: User = query<User>("userName == $0", user.userName).find().first()
            userResult.signedIn = true
        }

        val userResult: User = realmDb!!.query<User>("userName == $0", user.userName).find().first()

        return userResult
    }

    @Throws(AuthException::class)
    fun createUser(user: User) : User {
        if (realmDb == null) openRealm()
        // Check if user exists
        return try {
            realmDb!!.query<User>("userName == $0", user.userName).find().first()
            throw AuthException(AuthErrors.USERNAME_EXITS)
        } catch (e: NoSuchElementException) {
            // User Does not exits. Write to realm
            realmDb!!.writeBlocking {
                user.apply { signedIn = true }
                copyToRealm(user)
            }
            user
        }
    }

    fun getAllSignedInUser() : List<User> {
        if (realmDb == null) openRealm()
        val userResult: List<User> = realmDb!!.query<User>("signedIn == true").find()
        //realmDb.close()
        return userResult
    }

    fun appendLocation(time : Long, lat : Double, lon : Double, address : String) {

        if (currentUser == null) return

        Log.d(TAG, "Appending")

        if (realmDb == null) openRealm()

        realmDb!!.writeBlocking {
            val userResult: User = query<User>("userName == $0", currentUser!!.userName).find().first()
            userResult.locations.add(Location(time, lat, lon, address))
        }
    }

    fun updateCurrentUserLocations() : User {
        if (realmDb == null) openRealm()
        // Check if user exists
        val userResult: User = realmDb!!.query<User>("userName == $0", currentUser!!.userName).find().first()
        currentUser = userResult
        return currentUser!!
    }

}