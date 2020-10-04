package com.scheduler.incodetask.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.scheduler.incodetask.R
import com.scheduler.incodetask.camera.CameraPreview
import com.scheduler.incodetask.extensions.toBytes
import com.scheduler.incodetask.service.BitmapService
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {
    private val STORAGE_PERMISSION_REQUEST_CODE = 9003
    private val CAMERA_PERMISSION_REQUEST_CODE = 9002

    private var camera: Camera? = null
    private var cameraPreview: CameraPreview? = null

    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(coroutineJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }

    override fun onResume() {
        super.onResume()

        checkAndRequestPermission()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            } else {
                startCamera()
            }
        } else {
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty()) {
            var allPermissionsGiven = true
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    allPermissionsGiven = false
                }
            }

            if (allPermissionsGiven) {
                startCamera()
            }
        }
    }

    private fun startCamera() {
        camera = getCameraInstance()
        Log.e("sdfsdfsdf", "Starting camera")
        cameraPreview = CameraPreview(this, camera!!)

        cameraPreview.also {
            Log.e("sdfsdfsdf", "addingView to parent")
            cameraHolderLayout.addView(it)
        }

        captureButton.setOnClickListener {
            camera!!.takePicture(null, null, Camera.PictureCallback { photoBytes, _ ->

                val bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
//                val stream = ByteArrayOutputStream()
//
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//                val byteArray = stream.toByteArray()
//                checkAndRequestPermission()

                savePhotoAndFinish(bitmap)
            })
        }
    }

    private fun savePhotoAndFinish(bitmap: Bitmap) {
//        var stream: OutputStream? = null
//        var path = ""
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            val resolver = contentResolver
//            val contentValues = ContentValues()
//            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "imageBitmap.png");
//            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
//            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
//            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//            path = imageUri!!.path!!
//            stream = resolver.openOutputStream(imageUri)
//
//            path = Environment.DIRECTORY_PICTURES+"/imageBitmap.png"
//
//            Log.e("sfsdsdf", "Path ${imageUri.path}")
//            Log.e("sfsdsdf", "Path $imageUri")
//
//
//
//        } else {
//            val file = File(Environment.getExternalStorageDirectory().toString() + "imageBitmap" + ".png")
//            stream = FileOutputStream(file)
//            path = file.absolutePath
//
//        }
////        bitmap.compress(Bitmap.CompressFormat.PNG, 85, stream)
//        stream?.flush()
//        stream?.close()


        val pictureFile = getFile()
        coroutineScope.launch(Dispatchers.IO) {
            val resized = BitmapService().resize(bitmap, 1000, 1000)
            val byteArray = resized!!.toBytes()
            Log.e("sdfsdfsdf", "Created byte array")

            try {
                val fos = FileOutputStream(pictureFile)
                fos.write(byteArray)
                Log.e("sdfsdfsdf", "Finished writing closing stream")
                fos.close()
                setResult(Activity.RESULT_OK, Intent().apply { putExtra("sdfsdfsdf", pictureFile.absolutePath) })
                finish()
            } catch (e: FileNotFoundException) {
                Log.d("TAG", "File not found: ${e.message}")
            } catch (e: IOException) {
                Log.d("TAG", "Error accessing file: ${e.message}")
            }
        }

    }

    private fun getFile(): File {
        return getOutputMediaFile(MEDIA_TYPE_IMAGE) ?: run {
            Log.e("sdfsdfsdf", ("Error creating media file, check storage permissions"))
            throw Exception("Error creating file")
        }

    }

    private fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
//            }

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun getCameraInstance(): Camera? {
        return try {
            Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            throw e
        }
    }

    private fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
    }

    /** Create a File for saving an image or video */
    private fun getOutputMediaFile(type: Int): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "MyCameraApp"
        )
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        mediaStorageDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    Log.d("MyCameraApp", "failed to create directory")
                    return null
                }
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return when (type) {
            MEDIA_TYPE_IMAGE -> {
                File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp.jpg")
            }
            else -> null
        }
    }
}