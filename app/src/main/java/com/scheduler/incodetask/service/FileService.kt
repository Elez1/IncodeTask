package com.scheduler.incodetask.service

import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FileService {

    private val TAG = FileService::class.java.simpleName
    private val DEFAULT_FILE_NAME = "IMG_${getCurrentTimestamp()}.jpg"

    fun getOutputMediaFile(pictureName: String = DEFAULT_FILE_NAME): File? {
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "IncodeTask")

        mediaStorageDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    Log.d(TAG, "failed to create directory")
                    return null
                }
            }
        }

        return File("${mediaStorageDir.path}${File.separator}$pictureName")
    }

    private fun getCurrentTimestamp() = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

    fun saveBitmap(byteArray: ByteArray, file: File = getOutputMediaFile()!!) = GlobalScope.launch(Dispatchers.IO) {
        try {
            val fos = FileOutputStream(file)
            fos.write(byteArray)
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.d("TAG", "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.d("TAG", "Error accessing file: ${e.message}")
        }

    }

    fun deleteFile(file: File) {
        if (file.exists()) file.delete()
    }
}