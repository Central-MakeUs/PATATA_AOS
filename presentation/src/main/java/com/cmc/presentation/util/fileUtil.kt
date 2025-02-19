package com.cmc.presentation.util

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.cmc.domain.constants.ImageUploadPolicy
import com.cmc.domain.model.ImageMetadata
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

fun List<Uri>.toImageMetaDataList(context: Context): List<Result<ImageMetadata>> {
    return this.map { uri -> uriToImageMetadata(context, uri) }
}

fun uriToImageMetadata(context: Context, uri: Uri): Result<ImageMetadata> {
    return runCatching {
        val fileName = getFileNameFromUri(context, uri)
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val originalSize = getFileSizeFromUri(context, uri)

        val file = getFileFromUri(context, uri)

        // 5MB 이하로 될 때까지 리사이징 반복
        val resizedFile = if (ImageUploadPolicy.isSingleImageSizeValid(originalSize).not()) {
            resizeImageUntilValid(context, uri)
        } else {
            file
        }

        val finalSize = resizedFile.length()

        ImageMetadata(
            uri = resizedFile.toURI().toString(),
            fileName = fileName,
            mimeType = mimeType,
            fileSize = finalSize
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

fun getFileFromUri(context: Context, uri: Uri): File {
    val inputStream: InputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Failed to open URI")

    val fileName = getFileNameFromUri(context, uri)
    val tempFile = File(context.cacheDir, fileName)

    FileOutputStream(tempFile).use { outputStream ->
        inputStream.copyTo(outputStream)
    }

    return tempFile
}

/**
 * 5MB 이하가 될 때까지 이미지 크기와 품질을 조절하며 리사이징
 */
fun resizeImageUntilValid(context: Context, uri: Uri): File {
    var quality = 90
    var maxSize = 1440 // 시작 크기
    var resizedFile: File

    do {
        resizedFile = resizeImage(context, uri, maxSize, quality)
        val fileSize = resizedFile.length()
        if (ImageUploadPolicy.isSingleImageSizeValid(fileSize).not()) {
            // 품질과 크기를 점진적으로 줄이기
            quality -= 10
            maxSize -= 200
        }

    } while (ImageUploadPolicy.isSingleImageSizeValid(fileSize).not() && quality > 20 && maxSize > 600)

    if (ImageUploadPolicy.isSingleImageSizeValid(resizedFile.length()).not()) {
        throw IllegalStateException("이미지 리사이징 실패: 크기를 줄여도 5MB를 초과합니다.")
    }

    return resizedFile
}

/**
 * 주어진 비트맵을 maxSize 크기 및 quality 압축률로 리사이징
 */
fun resizeImage(context: Context, uri: Uri, maxSize: Int, quality: Int): File {
    val inputStream = context.contentResolver.openInputStream(uri) ?: throw IllegalArgumentException("Failed to open URI")
    val originalBitmap = BitmapFactory.decodeStream(inputStream)
    inputStream.close()

    if (originalBitmap == null) {
        throw IllegalArgumentException("Failed to decode bitmap from URI: $uri")
    }

    val resizedBitmap = scaleBitmap(originalBitmap, maxSize)

    val resizedFile = File(context.cacheDir, "resized_${UUID.randomUUID()}.jpg")
    FileOutputStream(resizedFile).use { outputStream ->
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    }

    return resizedFile
}

/**
 * 주어진 비트맵을 최대 크기로 리사이징
 */
fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val scale = maxSize.toFloat() / maxOf(width, height)

    if (scale >= 1) return bitmap // 크기가 이미 작으면 리사이징 X

    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()

    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}