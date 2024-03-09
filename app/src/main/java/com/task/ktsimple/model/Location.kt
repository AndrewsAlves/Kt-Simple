package com.task.ktsimple.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

/**
 * Created by Admin on 09,March,2024
 */

open class Location() : RealmObject {

    constructor(time : Long, lat : Double, lon : Double, address : String = "") : this() {
        this.time = time
        this.lat = lat
        this.lon = lon
        this.address = address
    }

    var time : Long = 0
    var lat : Double = 0.0
    var lon : Double = 0.0
    var address : String = ""
}

