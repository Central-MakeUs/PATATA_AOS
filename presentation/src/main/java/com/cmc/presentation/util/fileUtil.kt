package com.cmc.presentation.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import com.cmc.domain.model.ImageMetadata
import java.io.File
import java.util.UUID

fun List<Uri>.toImageMetaDataList(context: Context): List<Result<ImageMetadata>> {
    return this.map { uri -> uriToImageMetadata(context, uri) }
}

fun uriToImageMetadata(context: Context, uri: Uri): Result<ImageMetadata> {
    return runCatching {
        val fileName = getFileNameFromUri(context, uri)
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val fileSize = getFileSizeFromUri(context, uri)

        ImageMetadata(
            uri = uri.toString(),
            fileName = fileName,
            mimeType = mimeType,
            fileSize = fileSize,
        )
    }
}

fun getFileNameFromUri(context: Context, uri: Uri): String {
    var name: String? = null

    if (uri.scheme == "content") {
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && it.moveToFirst()) {
                name = it.getString(nameIndex)
            }
        }
    }

    if (name == null) {
        name = uri.path?.let { File(it).name }
    }

    return name ?: "temp_image_${UUID.randomUUID()}.jpg"
}

fun getFileSizeFromUri(context: Context, uri: Uri): Long {
    var fileSize: Long = 0
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        if (sizeIndex != -1 && cursor.moveToFirst()) {
            fileSize = cursor.getLong(sizeIndex)
        }
    }
    return fileSize
}