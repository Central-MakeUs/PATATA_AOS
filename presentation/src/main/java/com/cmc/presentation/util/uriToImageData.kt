package com.cmc.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import com.cmc.domain.base.exception.AppInternalException
import com.cmc.domain.model.ImageData
import com.cmc.presentation.model.ImageDataUiModel
import java.io.ByteArrayOutputStream

fun List<Uri>.toImageDataList(context: Context): List<Result<ImageDataUiModel>> {
    return this.map { uri -> uriToImageData(context, uri) }
}

fun uriToImageData(
    context: Context,
    uri: Uri,
    maxWidth: Int = 800,
    maxHeight: Int = 800,
    quality: Int = 80
): Result<ImageDataUiModel> {
    return runCatching {
        val resizedBitmap  = getResizedBitmap(context, uri, maxWidth, maxHeight)
            ?: throw AppInternalException.IOException("Failed to decode Bitmap from URI: $uri")

        val correctedBitmap = getCorrectedBitmap(context, uri, resizedBitmap)

        val byteArray = bitmapToByteArray(correctedBitmap, quality)
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"

        ImageDataUiModel(
            uri = uri,
            imageData = ImageData(
                fileName = fileName,
                mimeType = mimeType,
                byteArray = byteArray
            )
        )
    }
}

fun getResizedBitmap(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int): Bitmap? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null

    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeStream(inputStream, null, options)

    val scaleFactor = maxOf(1, minOf(options.outWidth / maxWidth, options.outHeight / maxHeight))

    val decodeOptions = BitmapFactory.Options().apply {
        inSampleSize = scaleFactor
    }
    inputStream.close()
    val newInputStream = context.contentResolver.openInputStream(uri) ?: return null
    return BitmapFactory.decodeStream(newInputStream, null, decodeOptions)
}

fun getCorrectedBitmap(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return bitmap
        val exif = ExifInterface(inputStream)

        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap // 회전 필요 없음
        }
    } catch (e: AppInternalException.IOException) {
        e.printStackTrace()
        bitmap // 실패 시 원본 그대로 반환
    }
}


// Bitmap 을 회전 하는 함수
private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun bitmapToByteArray(bitmap: Bitmap, quality: Int = 80): ByteArray {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    return outputStream.toByteArray()
}
