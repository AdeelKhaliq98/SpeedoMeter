package br.com.helpdev.velocimetroalerta.gps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import java.util.concurrent.TimeUnit

/**
 * Permissions <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"></uses-permission>
 *
 * @author Guilherme Biff Zarelli
 */
class Gps : LocationListener {

    var views: Int = 0

    private var location: Location? = null


    private var locationManager: LocationManager? = null
    var isPermission = false
        private set

    val isGpsEnable: Boolean
        get() {
            if (locationManager == null) {
                throw RuntimeException("No init Gps")
            }
            return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

    init {
        views = -1
        location = null
    }

    @SuppressLint("MissingPermission")
    fun init(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isPermission = true
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    }

    fun close() {
        try {
            if (locationManager == null) {

            }
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }

        locationManager?.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
        views = 0
    }


    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }

    fun getLocation(): Location? {
        try {
            if (locationManager == null) {

            }
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }

        views++
        return location
    }

}
