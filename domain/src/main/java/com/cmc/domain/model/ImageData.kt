package com.cmc.domain.model

data class ImageData(
    val fileName: String,
    val mimeType: String,
    val byteArray: ByteArray
) {
    val fileSize: Long
        get() = byteArray.size.toLong()

    override fun toString(): String {
        return "ImageData(fileName='$fileName', mimeType='$mimeType', byteArraySize=${byteArray.size})"
    }
}


