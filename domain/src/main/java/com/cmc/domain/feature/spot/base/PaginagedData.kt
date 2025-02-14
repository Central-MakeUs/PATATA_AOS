package com.cmc.domain.feature.spot.base

data class PaginatedData<T>(
    val totalCount: Int,        // 전체 데이터 개수
    val currentPage: Int,       // 현재 페이지 번호
    val totalPages: Int,        // 전체 페이지 개수
    val items: List<T> // 현재 페이지의 Spot 리스트
)