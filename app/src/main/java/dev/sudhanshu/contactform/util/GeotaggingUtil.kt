package dev.sudhanshu.contactform.util


import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager

object GeotaggingUtil {
    @SuppressLint("MissingPermission")
    fun getGeolocation(): Pair<Double, Double>? {
        val locationManager = ActivityContextHolder.getActivityContext()!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location: Location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) ?: return null
        return Pair(location.latitude, location.longitude)
    }
}
