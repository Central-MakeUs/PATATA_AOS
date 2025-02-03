package com.cmc.data.feature.location

import android.content.Context
import com.cmc.domain.feature.location.Location
import com.cmc.domain.feature.location.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @Suppress("MissingPermission")
    override suspend fun getCurrentLocation(): Result<Location> {
        return runCatching {
            val location = suspendCancellableCoroutine<android.location.Location> { continuation ->
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        continuation.resume(location)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            Location(location.latitude, location.longitude)
        }
    }
}