package com.example.eventmap.utils

import android.content.Context
import android.content.Intent
import com.example.eventmap.services.TrackingService
import com.example.eventmap.utils.Constants.ACTION_PAUSE_SERVICE
import com.example.eventmap.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.eventmap.utils.Constants.ACTION_STOP_SERVICE


fun startOrResumeTrackingService(context: Context) =
    Intent(context, TrackingService::class.java).also {
        it.action = ACTION_START_OR_RESUME_SERVICE
        context.startService(it)
    }

fun pauseTrackingService(context: Context) =
    Intent(context, TrackingService::class.java).also {
        it.action = ACTION_PAUSE_SERVICE
        context.startService(it)
    }

fun stopTrackingService(context: Context) =
    Intent(context, TrackingService::class.java).also {
        it.action = ACTION_STOP_SERVICE
        context.startService(it)
    }