package com.task.ktsimple.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

/**
 * Created by Admin on 08,March,2024
 */
open class User() : RealmObject {

    constructor(userName : String = "", passWord : String = "") : this() {
        this.userName = userName
        this.passWord = passWord
    }

    @PrimaryKey var userName : String = ""
    var passWord : String = ""
    var signedIn : Boolean = false
    var avatar = (0..5).random()
}
