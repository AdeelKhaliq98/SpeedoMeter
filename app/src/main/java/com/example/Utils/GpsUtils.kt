package com.example.Utils

import android.location.Location
import android.util.Log
import kotlin.math.roundToInt

object GpsUtils {

    private var lat1: Location? = null
    private var calculatedDistance: Double = 0.00
    private var startTime: Long? = null
    private var endTime: Long? = null
    private var averageSpeed: Double = 0.0
    private var timeDiff:Long ?=null

    fun distanceCalculate(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        var longdiff: Double = lng1 - lng2
        var distance: Double =
            Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(
                deg2rad(lat2)
            ) * Math.cos(deg2rad(longdiff));
        distance = Math.acos(distance)
        //into degree
        distance = rad2deg(distance)
        // In Miles
        distance = distance * 60 * 1.1515
        //In kilometers
        distance = distance * 1.609344

        return distance
    }

    private fun rad2deg(distance: Double): Double {
        return (distance * 180.0 / Math.PI)
    }

    private fun deg2rad(distance: Double): Double {
        return (distance * Math.PI / 180.0)
    }

    fun calculateDistances(location: Location): Double {
        if (lat1 == null && startTime == null) {
            lat1 = location
            startTime = System.currentTimeMillis()
        } else {
            lat1 = lat1
        }
        val distance2 = distanceCalculate(
            lat1!!.latitude,
            lat1!!.longitude,
            location.latitude,
            location.longitude
        )
        endTime = System.currentTimeMillis()
        calculatedDistance = Math.floor(distance2 * 100) / 100
        if (calculatedDistance>=0.01)
        {
            return calculatedDistance
        }
        else
            return 0.0
    }

    fun locationCheck(location: Location?) {
        lat1 = location
        calculatedDistance= 0.0
        startTime = null
        endTime = null
        timeDiff=null
        averageSpeed=0.0
    }

    fun calAvgSpeed(): Double {
        Log.v("CheckAverageSpeed","$averageSpeed,$startTime,$endTime,$timeDiff")
        timeDiff = startTime?.let { endTime?.minus(it) }
        averageSpeed = (calculatedDistance * 3600000) / timeDiff!!
        return averageSpeed
    }
}