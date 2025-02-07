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
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null) {
                            continuation.resume(task.result)
                        } else {
                            continuation.resume(createMockLocation())
                        }
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            Location(location.latitude, location.longitude)
        }
    }

    fun createMockLocation(latitude: Double = 37.489479, longitude: Double = 126.724519, provider: String = "gps"): android.location.Location {
        return android.location.Location(provider).apply {
            this.latitude = latitude
            this.longitude = longitude
            this.accuracy = 10f  // 임의의 정확도 (단위: 미터)
            this.altitude = 50.0  // 임의의 고도 (단위: 미터)
            this.time = System.currentTimeMillis() // 현재 시간 설정
        }
    }
}