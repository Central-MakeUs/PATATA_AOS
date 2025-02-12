package com.cmc.presentation.model

import android.net.Uri
import com.cmc.domain.model.ImageData

data class ImageDataUiModel(
    val uri: Uri,
    val imageData: ImageData
)

fun ImageDataUiModel.toDomain(): ImageData {
    return imageData
}

fun List<ImageDataUiModel>.toUris(): List<Uri> {
    return this.map { it.uri }
}

