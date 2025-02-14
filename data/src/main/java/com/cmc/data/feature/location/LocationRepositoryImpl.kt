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

internal class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @Suppress("MissingPermission")
    override suspend fun getCurrentLocation(): Result<Location> {
        return try {
            suspendCancellableCoroutine { continuation ->
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (continuation.isActive) {
                            continuation.resume(Result.success(location?.let {
                                Location(it.latitude, it.longitude)
                            } ?: createMockLocation()))
                        }
                    }
                    .addOnFailureListener { exception ->
                        if (continuation.isActive) {
                            continuation.resume(Result.failure(exception))
                        }
                    }
            }
        } catch (e: SecurityException) {
            Result.failure(e)  // 권한이 없을 경우 명확하게 예외 처리
        } catch (e: Exception) {
            Result.failure(e)  // 기타 예외 처리
        }
    }

    private fun createMockLocation(latitude: Double = 37.489479, longitude: Double = 126.724519): Location {
        return Location(latitude, longitude)
    }
}