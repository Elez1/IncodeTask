package com.scheduler.incodetask.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.scheduler.incodetask.R
import com.scheduler.incodetask.camera.CameraPreview
import com.scheduler.incodetask.extensions.toBytes
import com.scheduler.incodetask.service.BitmapService
import com.scheduler.incodetask.service.FileService
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CameraActivity : BaseActivity() {
    private val TAG = CameraActivity::class.java.simpleName
    private val STORAGE_PERMISSION_REQUEST_CODE = 9003
    private val CAMERA_PERMISSION_REQUEST_CODE = 9002
    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(coroutineJob)

    private val fileService = FileService()
    private val bitmapService = BitmapService()

    private var camera: Camera? = null
    private var cameraPreview: CameraPreview? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        captureButton.setOnClickListener {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_REQUEST_CODE) {
                takePhoto()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        requestPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_REQUEST_CODE) {
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                finish()
            }
        }

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            } else {
                finish()
            }
        }
    }

    private fun requestPermission(permission: String, requestCode: Int, block: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permission), requestCode)
                return
            }
            block()
        } else {
            block()
        }
    }

    private fun startCamera() {
        camera = getCameraInstance()
        cameraPreview = CameraPreview(this, camera!!)
        cameraPreview.also {
            cameraHolderLayout.addView(it)
        }
    }

    private fun takePhoto() {
        showSpinner()
        camera!!.takePicture(null, null, Camera.PictureCallback { photoBytes, _ ->
            val bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
            savePhotoAndFinish(bitmap)
        })
    }

    private fun savePhotoAndFinish(bitmap: Bitmap) = coroutineScope.launch(Dispatchers.IO) {
        val pictureFile = fileService.getOutputMediaFile().also { Log.d(TAG, it.toString()) } ?: return@launch
        val resized = bitmapService.resize(bitmap, 1000, 1000)
        val byteArray = resized!!.toBytes()

        fileService.saveBitmap(byteArray, pictureFile)
        hideSpinner()
        setResult(Activity.RESULT_OK, Intent().apply { putExtra(MainActivity.PHOTO_PATH_KEY, pictureFile.absolutePath) })
        finish()
    }


    private fun getCameraInstance(): Camera? {
        return try {
            Camera.open()
        } catch (e: Exception) {
            throw e
        }
    }

    override fun onStop() {
        camera?.release()
        if (coroutineJob.isActive) {
            coroutineJob.cancel()
        }

        super.onStop()
    }
}