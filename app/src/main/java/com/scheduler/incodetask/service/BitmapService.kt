package com.scheduler.incodetask.service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.scheduler.incodetask.extensions.rotate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class BitmapService {

    suspend fun getBitmapFromPath(path: String, block: (bitmap: Bitmap) -> Unit) = withContext(Dispatchers.IO) {
        val b = BitmapFactory.decodeFile(path)
        val rotated = b.rotate(-90)
        withContext(Dispatchers.Main) {
            block(rotated.await())
            b.recycle()
        }
    }

    fun resize(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap? {
        return if (maxHeight > 0 && maxWidth > 0) {
            val width = image.width
            val height = image.height
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > ratioBitmap) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
        } else {
            image
        }
    }
}