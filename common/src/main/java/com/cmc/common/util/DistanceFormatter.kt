package com.cmc.common.util

import java.util.Locale

object DistanceFormatter {

    /**
     * 서버에서 km 단위로 제공된 거리를 m 또는 km로 변환하여 반환한다.
     *
     * @param distanceInKm 서버에서 제공된 거리 (단위: km)
     * @return 거리 문자열 (예: "500 m", "1.2 km")
     */
    fun formatDistance(distanceInKm: Double): String {
        return if (distanceInKm < 1) {
            // 1 km 미만은 m 단위로 변환
            val distanceInMeters = (distanceInKm * 1000).toInt()
            "%dm".format(Locale.US, distanceInMeters)
        } else {
            // 1 km 이상은 소수점 한 자리까지만 표시
            "%.1fkm".format(Locale.US, distanceInKm)
        }
    }
}
