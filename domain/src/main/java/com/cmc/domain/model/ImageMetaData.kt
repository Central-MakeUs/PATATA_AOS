package com.cmc.domain.model

data class ImageMetadata(
    val uri: String,
    val fileName: String,
    val mimeType: String,
    val fileSize: Long,
) {
    companion object {
        fun getDefault(): ImageMetadata {
            return ImageMetadata(
                uri = "",
                fileName = "",
                mimeType = "",
                fileSize = 0L,
            )
        }
    }
}
