package com.cmc.domain.constants

object ImageUploadPolicy {
    const val MAX_IMAGE_SIZE_MB = 5  // 개별 이미지 최대 용량 (MB)
    const val MAX_TOTAL_IMAGE_SIZE_MB = 10 // 전체 업로드 이미지 최대 용량 (MB)
    const val MAX_IMAGE_COUNT = 3 // 최대 업로드 이미지 개수

    fun isSingleImageSizeValid(sizeInBytes: Long): Boolean {
        return sizeInBytes <= (MAX_IMAGE_SIZE_MB * 1024 * 1024)
    }

    fun isTotalImageSizeValid(totalSizeInBytes: Long): Boolean {
        return totalSizeInBytes <= (MAX_TOTAL_IMAGE_SIZE_MB * 1024 * 1024)
    }

    fun isImageCountValid(count: Int): Boolean {
        return count <= MAX_IMAGE_COUNT
    }
}
