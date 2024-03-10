package com.task.ktsimple.interfaces

import com.task.ktsimple.model.User

/**
 * Created by Admin on 08,March,2024
 */

interface ItemClickedListener {
    fun itemClicked(index : Int, item : Any)
    fun clickedSignOut(index : Int, item : User){}
}