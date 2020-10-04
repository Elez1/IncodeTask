package com.scheduler.incodetask.extensions

import android.graphics.Bitmap
import android.graphics.Matrix
import android.hardware.Camera
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.ByteArrayOutputStream


fun Bitmap.toBytes(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}

fun Bitmap.rotate(angle: Int) = GlobalScope.async(Dispatchers.IO) {
    val matrix = Matrix()
    matrix.postRotate(angle.toFloat())
    Bitmap.createBitmap(this@rotate, 0, 0, width, height, matrix, true)
}

fun Camera.Size.getString() =  "Width = $width, height = $height"