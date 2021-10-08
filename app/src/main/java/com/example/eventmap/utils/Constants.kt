package com.example.eventmap.utils

object Constants {
    const val USERS_DB = "users_db"
    const val EVENTS_DB = "events_db"
    const val FCM_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY = "AAAARwgY7e0:APA91bG0WopywlvO9MR_i7M6S78QGA3AVurN6iaYRqB2QYuxwUEqATfDonGz4Fq4zzv3QNXOL5PUcy5bqOSnTkd44D1J1YZL0Nq0yyprh5wE5I7VqfopucOVgjUSnf1DPhZxdU9X6xjV"
    const val CONTENT_TYPE = "application/json"

    const val LOCATION_PERMISSION_REQUEST_CODE = 123

    //action consts
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

    //location request consts
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val NEARBY_USERS_NOTIFICATION_ID = 3
    const val NEARBY_EVENTS_NOTIFICATION_ID = 2
    const val TRACKING_NOTIFICATION_ID = 1

    const val DISTANCE_FOR_NEARBY_TRACKING = 1000f
}