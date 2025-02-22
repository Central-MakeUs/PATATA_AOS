package com.cmc.common.util

import android.net.Uri

object DeepLinkUtil {
    private const val SCHEME = "patata"
    private const val HOST_ADD_SPOT = "add_spot"

    fun createAddSpotUri(addressName: String, latitude: Double, longitude: Double): Uri {
        return Uri.Builder()
            .scheme(SCHEME)
            .authority(HOST_ADD_SPOT)
            .appendQueryParameter("addressName", addressName)
            .appendQueryParameter("latitude", latitude.toString())
            .appendQueryParameter("longitude", longitude.toString())
            .build()
    }
}
