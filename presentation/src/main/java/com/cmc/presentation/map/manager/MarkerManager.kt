package com.cmc.presentation.map.manager

import android.util.Log
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.model.SpotCategoryItem
import com.cmc.presentation.map.model.SpotWithMapUiModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

class MarkerManager(
    private val naverMap: NaverMap,
    private val onMarkerClickListener: ((SpotWithMapUiModel) -> Unit)? = null,
) {
    private val markerMap: MutableMap<SpotWithMapUiModel, Marker> = mutableMapOf()

    fun updateMarkersWithData(newDataList: List<SpotWithMapUiModel>?) {
        markerMap.values.forEach { it.map = null }
        if (newDataList.isNullOrEmpty()) return

        // 추가할 데이터 처리
        newDataList.forEach { data ->
            markerMap[data] = Marker().apply {
                position = LatLng(data.latitude, data.longitude)
                icon = OverlayImage.fromResource(
                    SpotCategoryItem(SpotCategory.fromId(data.categoryId))
                        .getMarkerIcon()
                )
                map = naverMap
                setOnClickListener {
                    onMarkerClickListener?.invoke(data)
                    true
                }
            }
        }
    }
}
