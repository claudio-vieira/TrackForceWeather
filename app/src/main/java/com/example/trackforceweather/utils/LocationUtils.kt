package com.example.trackforceweather.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.example.trackforceweather.domain.model.LocationData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object LocationUtils {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
    context: Context,
    onLocationReceived: (LocationData?) -> Unit
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // 10 seconds
        ).apply {
            setMinUpdateDistanceMeters(100f)
            setMaxUpdateDelayMillis(15000L) // 15 seconds
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { location ->
                    onLocationReceived(
                        LocationData(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }

        // First try to get last known location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onLocationReceived(
                        LocationData(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                } else {
                    // If no last known location, request new location
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                }
            }
            .addOnFailureListener {
                onLocationReceived(null)
            }
    }
}