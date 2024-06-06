package com.mikhail_R_gps_tracker.gpsassistant.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

@SuppressLint("SimpleDateFormat")
object TimeUtils {
    private val timeFormatter = SimpleDateFormat("HH:mm:ss:SSS")
    private val dateFormatter = SimpleDateFormat("dd/MM/yyy HH:mm")

    fun getTime(timeInMillis: Long): String{
        val cv = Calendar.getInstance()
        timeFormatter.timeZone = TimeZone.getTimeZone("UTC")
        cv.timeInMillis = timeInMillis
        return timeFormatter.format(cv.time)
    }

    fun getDate(): String{
        val cv = Calendar.getInstance()
        return dateFormatter.format(cv.time)
    }
}