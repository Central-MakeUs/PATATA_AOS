package com.cmc.data.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.cmc.domain.base.exception.AppInternalException
import java.io.File
import java.io.FileOutputStream

fun uriToFile(context: Context, uri: Uri, fileName: String): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw AppInternalException.IOException("Failed to open URI")

    val cacheDir = context.externalCacheDir ?: context.cacheDir
    val file = File(cacheDir, fileName)

    val outputStream = file.outputStream()

    inputStream.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
            output.flush()
        }
    }
    outputStream.close()

    return getCorrectedFile(context, file)
}


private fun getCorrectedFile(context: Context, file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path) ?: return file
    val exif = ExifInterface(file.path)

    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

    val rotatedBitmap = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
        else -> bitmap
    }

    return bitmapToFile(context, rotatedBitmap, file.name)
}
private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String, quality: Int = 80): File {
    val file = File(context.cacheDir, fileName)

    // 기존 파일이 있다면 삭제
    if (file.exists()) {
        file.delete()
    }

    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

    outputStream.flush()
    outputStream.close()

    return file
}