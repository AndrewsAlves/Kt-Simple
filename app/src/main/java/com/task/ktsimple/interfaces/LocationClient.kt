package com.task.ktsimple.interfaces

/**
 * Created by Admin on 09,March,2024
 */

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(interval: Long): Flow<Location>

    class LocationException(message: String): Exception()
}