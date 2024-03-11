package com.task.ktsimple.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by Admin on 11,March,2024
 */
class NotificationStopBR : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            context?.startService(this)
        }
    }
}