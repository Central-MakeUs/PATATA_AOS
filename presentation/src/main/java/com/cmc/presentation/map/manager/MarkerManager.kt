package com.cmc.presentation.map.manager

import android.content.Context
import androidx.core.content.ContextCompat
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.map.model.SpotWithMapUiModel
import com.cmc.presentation.model.SpotCategoryItem
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

class MarkerManager(
    private val context: Context,
    private val naverMap: NaverMap,
    private val onMarkerClickListener: ((SpotWithMapUiModel) -> Unit)? = null,
) {
    private val markerMap: MutableMap<SpotWithMapUiModel, Marker> = mutableMapOf()
    private var circleOverlay: CircleOverlay? = null

    fun updateMarkersWithData(newDataList: List<SpotWithMapUiModel>?) {
        markerMap.values.forEach { it.map = null }
        if (newDataList.isNullOrEmpty()) return

        // 추가할 데이터 처리
        newDataList.forEach { data ->
            markerMap[data] = Marker().apply {
                position = LatLng(data.latitude, data.longitude)

                val markerDrawable = try {
                    SpotCategoryItem(SpotCategory.fromId(data.categoryId))
                        .getMarkerIcon()
                } catch (e: Exception) {
                    DEFAULT_MARKER_ICON
                }
                icon = OverlayImage.fromResource(markerDrawable)
                map = naverMap
                setOnClickListener {
                    onMarkerClickListener?.invoke(data)
                    true
                }
            }
        }
    }

    fun showRegistrationLimitArea(centerLatLng: LatLng) {
        // 기존 원 제거
        circleOverlay?.map = null

        // 새 원 생성
        circleOverlay = CircleOverlay().apply {
            center = centerLatLng
            radius = 100.0 // 반경 100m
            color = ContextCompat.getColor(context, com.cmc.design.R.color.blue_20_trans_50)
            outlineColor = ContextCompat.getColor(context, com.cmc.design.R.color.blue_100)
            outlineWidth = 5
            map = naverMap
        }
    }

    fun clearRegistrationLimitArea() {
        circleOverlay?.map = null
    }

    companion object {
        val DEFAULT_MARKER_ICON = R.drawable.ic_pin_inactive
    }
}
